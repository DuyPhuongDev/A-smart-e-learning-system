"""
Model Training Module
Trains Ridge regression model for 4-point scale grade prediction.
Dataset is already in 4-point scale (0.0-4.0).
"""

import json
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
from skl2onnx import to_onnx
from skl2onnx.common.data_types import FloatTensorType

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
        "subject_id",
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

    # Enforce column order for metadata and ONNX consistency (reduced feature set)
    desired_cols = [
        "sem_credits",
        "sem_credits_squared",
        "retake_no",
        "num_semesters_prior",
        "cumulative_grade_avg",
        "previous_sem_grade_avg",
        "subject_hist_median_smooth",
        "relative_avg_course_grade",
    ]
    missing = [c for c in desired_cols if c not in X.columns]
    if missing:
        raise ValueError(f"Missing expected feature columns: {missing}")
    X = X[desired_cols]

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
            leakage_cols=[],
            prefix="[TRAIN]"
        )
        X_test, y_test = prepare_X_y(
            df_test,
            target_col="course_grade",
            leakage_cols=[],
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
        baseline_train = df_train["subject_hist_median_smooth"].astype(float).to_numpy()
        baseline_test = df_test["subject_hist_median_smooth"].astype(float).to_numpy()

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

    def convert_to_onnx(self, model: Pipeline, feature_cols: List[str]) -> bytes:
        """
        Convert scikit-learn pipeline to ONNX format.

        Parameters
        ----------
        model : Pipeline
            Trained sklearn pipeline (SimpleImputer + StandardScaler + Ridge)
        feature_cols : list
            Feature column names (for documentation)

        Returns
        -------
        bytes
            ONNX model as bytes
        """
        n_features = len(feature_cols)

        # Define input type: float32[batch_size, n_features]
        initial_type = [('float_input', FloatTensorType([None, n_features]))]

        logger.info(f"Converting model to ONNX with {n_features} features")

        # Convert to ONNX
        onnx_model = to_onnx(
            model,
            initial_types=initial_type,
            target_opset=15,  # ONNX opset version compatible with ONNX Runtime Java 1.16.3
        )

        logger.info("Successfully converted model to ONNX format")
        return onnx_model.SerializeToString()

    def save_and_upload_model(
        self,
        model: Pipeline,
        feature_cols: List[str],
        metrics: Dict[str, Any],
        model_name: str,
        timestamp: str,
    ) -> Tuple[str, str]:
        """
        Save model in both .joblib and .onnx formats, upload to S3.

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
            (S3 path for .onnx, S3 path for .joblib)
        """
        # Create payload for .joblib (backup/reference)
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

        base_filename = f"{model_name}_{timestamp.replace(':', '-')}"

        # 1. Save .joblib (backup)
        joblib_filename = f"{base_filename}.joblib"
        joblib_local_path = os.path.join(self.temp_dir, joblib_filename)
        logger.info(f"Saving .joblib to {joblib_local_path}")
        joblib.dump(payload, joblib_local_path)

        # 2. Convert to ONNX
        logger.info("Converting model to ONNX format...")
        onnx_bytes = self.convert_to_onnx(model, feature_cols)

        # 3. Save ONNX
        onnx_filename = f"{base_filename}.onnx"
        onnx_local_path = os.path.join(self.temp_dir, onnx_filename)
        logger.info(f"Saving .onnx to {onnx_local_path}")
        with open(onnx_local_path, 'wb') as f:
            f.write(onnx_bytes)

        # 4. Save metadata JSON (for Java to read feature_cols and metrics)
        metadata = {
            "feature_cols": feature_cols,
            "metrics": metrics,
            "trained_at": payload["trained_at"],
            "model_type": payload["model_type"],
            "preprocessing": payload["preprocessing"],
            "training_config": payload["training_config"],
        }
        metadata_filename = f"{base_filename}_metadata.json"
        metadata_local_path = os.path.join(self.temp_dir, metadata_filename)
        logger.info(f"Saving metadata to {metadata_local_path}")
        with open(metadata_local_path, 'w') as f:
            json.dump(metadata, f, indent=2)

        # 5. Upload all files to S3
        try:
            # Upload .onnx (primary)
            onnx_s3_key = f"{Config.S3_MODELS_PREFIX}{onnx_filename}"
            onnx_s3_path = f"s3://{Config.S3_BUCKET}/{onnx_s3_key}"
            logger.info(f"Uploading .onnx to {onnx_s3_path}")
            self.s3_client.upload_file(onnx_local_path, Config.S3_BUCKET, onnx_s3_key)

            # Upload .joblib (backup)
            joblib_s3_key = f"{Config.S3_MODELS_PREFIX}{joblib_filename}"
            joblib_s3_path = f"s3://{Config.S3_BUCKET}/{joblib_s3_key}"
            logger.info(f"Uploading .joblib to {joblib_s3_path}")
            self.s3_client.upload_file(joblib_local_path, Config.S3_BUCKET, joblib_s3_key)

            # Upload metadata
            metadata_s3_key = f"{Config.S3_MODELS_PREFIX}{metadata_filename}"
            logger.info(f"Uploading metadata to S3")
            self.s3_client.upload_file(metadata_local_path, Config.S3_BUCKET, metadata_s3_key)

            logger.info("Successfully uploaded all model files to S3")

            # Cleanup local files
            os.remove(onnx_local_path)
            os.remove(joblib_local_path)
            os.remove(metadata_local_path)
            logger.info("Cleaned up local model files")

            # Return ONNX path as primary (Java will use this)
            return onnx_s3_path, joblib_s3_path

        except ClientError as e:
            logger.error(f"Failed to upload model to S3: {e}")
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
