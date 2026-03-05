"""
Model Training Module
Trains Ridge regression model for 4-point scale grade prediction.
Dataset is already in 4-point scale (0.0-4.0).
"""

import logging
import os
from datetime import datetime, timezone
from typing import Tuple, List, Dict, Any

import boto3
import joblib
import numpy as np
import pandas as pd
from botocore.exceptions import ClientError
from sklearn.impute import SimpleImputer
from sklearn.linear_model import Ridge
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

from .config import Config

logger = logging.getLogger(__name__)


def prepare_X_y(
    df: pd.DataFrame,
    target_col: str,
    leakage_cols: List[str],
    prefix: str = "",
) -> Tuple[pd.DataFrame, pd.Series]:
    """
    Prepare feature matrix X and target vector y from DataFrame.
    Follows train_model.py logic exactly.
    """
    df = df.copy()

    if target_col not in df.columns:
        raise ValueError(f"Column '{target_col}' not found in dataframe!")

    # Extract target
    y = df[target_col].astype(float)
    df_features = df.drop(columns=[target_col])

    # Drop ID columns and leakage columns
    base_drop_cols = [
        "id",
        "student_id",
        "semester_id",
        "course_id",
        "created_at",
        "version_id",  # Added from entity structure
    ]

    drop_cols = base_drop_cols + leakage_cols

    for col in drop_cols:
        if col in df_features.columns:
            df_features.drop(columns=[col], inplace=True)

    X = df_features

    # Convert boolean to int
    bool_cols = X.select_dtypes(include=["bool", "boolean"]).columns.tolist()
    if bool_cols:
        logger.info(f"{prefix} Converting boolean cols to int: {bool_cols}")
        X[bool_cols] = X[bool_cols].astype(int)

    # Drop non-numeric columns
    non_numeric_cols = [
        c for c in X.columns
        if not np.issubdtype(X[c].dtype, np.number)
    ]
    if non_numeric_cols:
        logger.warning(f"{prefix} Dropping non-numeric columns: {non_numeric_cols}")
        X = X.drop(columns=non_numeric_cols)

    return X, y


def compute_metrics(y_true, y_pred) -> Dict[str, float]:
    """Compute MAE, RMSE, R²."""
    mae = mean_absolute_error(y_true, y_pred)
    mse = mean_squared_error(y_true, y_pred)
    rmse = mse ** 0.5
    r2 = r2_score(y_true, y_pred)

    return {
        "mae": float(mae),
        "rmse": float(rmse),
        "r2": float(r2),
    }


def estimate_error_distribution(y_true, y_pred) -> Dict[str, float]:
    """Estimate normal distribution parameters for prediction errors."""
    diff = y_pred - y_true
    mu_error = float(np.mean(diff))
    sigma_error = float(np.std(diff, ddof=1))
    n = int(diff.shape[0])

    lower_95 = mu_error - 1.96 * sigma_error
    upper_95 = mu_error + 1.96 * sigma_error

    return {
        "mu_error": mu_error,
        "sigma_error": sigma_error,
        "n": n,
        "lower_95": lower_95,
        "upper_95": upper_95,
    }


class ModelTrainer:
    """Handles model training, evaluation, and S3 upload."""

    def __init__(self):
        self.s3_client = boto3.client('s3', region_name=Config.AWS_REGION)
        self.temp_dir = Config.TEMP_DIR

    def train(
        self,
        df_train: pd.DataFrame,
        df_test: pd.DataFrame,
    ) -> Tuple[Pipeline, List[str], Dict[str, Any]]:
        """
        Train Ridge regression model and compute metrics.
        Dataset is already in 4-point scale (0.0-4.0).

        Returns
        -------
        model : Pipeline
            Trained sklearn pipeline
        feature_cols : list
            List of feature column names
        metrics : dict
            Metrics dictionary with train/test results
        """
        logger.info("Starting model training on 4-point scale...")

        # Prepare features and target (already in 4-point scale)
        X_train, y_train = prepare_X_y(
            df_train,
            target_col="course_grade",
            leakage_cols=["target_gap"],
            prefix="[TRAIN]"
        )
        X_test, y_test = prepare_X_y(
            df_test,
            target_col="course_grade",
            leakage_cols=["target_gap"],
            prefix="[TEST]"
        )

        logger.info(f"Feature shape - Train: {X_train.shape}, Test: {X_test.shape}")

        # Build and train pipeline
        pipeline = Pipeline(
            steps=[
                ("imputer", SimpleImputer(strategy="median")),
                ("scaler", StandardScaler()),
                ("model", Ridge(alpha=Config.RIDGE_ALPHA)),
            ]
        )

        logger.info("Training Ridge model on 4-point scale...")
        pipeline.fit(X_train, y_train)

        # Predictions (4-point scale)
        y_pred_train = pipeline.predict(X_train)
        y_pred_test = pipeline.predict(X_test)

        # Get baseline and true grades (4-point scale)
        true_grade_train = df_train["course_grade"].astype(float).to_numpy()
        true_grade_test = df_test["course_grade"].astype(float).to_numpy()
        baseline_train = df_train["course_hist_median_smooth"].astype(float).to_numpy()
        baseline_test = df_test["course_hist_median_smooth"].astype(float).to_numpy()

        # Compute metrics on 4-point scale
        baseline_train_metrics = compute_metrics(true_grade_train, baseline_train)
        baseline_test_metrics = compute_metrics(true_grade_test, baseline_test)
        model_train_metrics = compute_metrics(true_grade_train, y_pred_train)
        model_test_metrics = compute_metrics(true_grade_test, y_pred_test)

        error_dist_train = estimate_error_distribution(true_grade_train, y_pred_train)
        error_dist_test = estimate_error_distribution(true_grade_test, y_pred_test)

        logger.info(f"Train metrics: MAE={model_train_metrics['mae']:.4f}, "
                   f"RMSE={model_train_metrics['rmse']:.4f}, R²={model_train_metrics['r2']:.4f}")
        logger.info(f"Test metrics: MAE={model_test_metrics['mae']:.4f}, "
                   f"RMSE={model_test_metrics['rmse']:.4f}, R²={model_test_metrics['r2']:.4f}")

        # Build metrics dictionary
        metrics = {
            "train": {
                "baseline": baseline_train_metrics,
                "model": model_train_metrics,
                "error_distribution": error_dist_train,
            },
            "test": {
                "baseline": baseline_test_metrics,
                "model": model_test_metrics,
                "error_distribution": error_dist_test,
            },
        }

        feature_cols = X_train.columns.tolist()
        return pipeline, feature_cols, metrics

    def save_and_upload_model(
        self,
        model: Pipeline,
        feature_cols: List[str],
        metrics: Dict[str, Any],
        model_name: str,
        timestamp: str,
    ) -> Tuple[str, str]:
        """
        Save model with metadata to local file, then upload to S3.

        Parameters
        ----------
        model : Pipeline
            Trained sklearn pipeline
        feature_cols : list
            Feature column names
        metrics : dict
            Full metrics dictionary
        model_name : str
            Model name from SQS message
        timestamp : str
            Timestamp from SQS message

        Returns
        -------
        tuple[str, str]
            (S3 path, local file path) - local path for cleanup on error
        """
        # Create payload
        payload = {
            "model": model,
            "feature_cols": feature_cols,
            "trained_at": datetime.now(timezone.utc).isoformat(),
            "strategy": "raw_grade_4point",
            "target_col": "course_grade",
            "scale": "4-point (0.0-4.0)",
            "model_type": "RidgeRegression(4-point scale)",
            "preprocessing": {
                "imputer": "SimpleImputer(strategy='median')",
                "scaler": "StandardScaler",
            },
            "metrics": metrics,
            "training_config": {
                "ridge_alpha": Config.RIDGE_ALPHA,
                "test_size": Config.TEST_SIZE,
                "random_state": Config.RANDOM_STATE,
            },
        }

        # Save to local temp file
        local_filename = f"{model_name}_{timestamp.replace(':', '-')}.joblib"
        local_path = os.path.join(self.temp_dir, local_filename)

        logger.info(f"Saving model to {local_path}")
        joblib.dump(payload, local_path)

        # Upload to S3
        s3_key = f"{Config.S3_MODELS_PREFIX}{local_filename}"
        s3_path = f"s3://{Config.S3_BUCKET}/{s3_key}"

        logger.info(f"Uploading model to {s3_path}")
        try:
            self.s3_client.upload_file(local_path, Config.S3_BUCKET, s3_key)
            logger.info(f"Successfully uploaded model to S3")

            # Cleanup local file after successful upload
            os.remove(local_path)
            logger.info(f"Cleaned up local model file: {local_path}")

            return s3_path, None  # None indicates file was cleaned up
        except ClientError as e:
            logger.error(f"Failed to upload model to S3: {e}")
            # Return local path for cleanup in finally block
            raise
        except Exception as e:
            logger.error(f"Unexpected error during model save/upload: {e}")
            raise

    def cleanup(self, file_path: str):
        """Remove temporary model file."""
        try:
            if file_path and os.path.exists(file_path):
                os.remove(file_path)
                logger.info(f"Cleaned up temporary model file: {file_path}")
        except Exception as e:
            logger.warning(f"Failed to cleanup model file {file_path}: {e}")
