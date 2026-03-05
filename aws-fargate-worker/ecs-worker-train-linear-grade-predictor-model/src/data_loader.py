"""
Data Loading Module
Downloads CSV from S3 and prepares train/test splits.
"""

import logging
import os
from typing import Tuple

import boto3
import pandas as pd
from botocore.exceptions import ClientError
from sklearn.model_selection import GroupShuffleSplit

from .config import Config

logger = logging.getLogger(__name__)


class DataLoader:
    """Handles downloading and preparing training data from S3."""

    def __init__(self):
        self.s3_client = boto3.client('s3', region_name=Config.AWS_REGION)
        self.temp_dir = Config.TEMP_DIR

        # Ensure temp directory exists
        os.makedirs(self.temp_dir, exist_ok=True)

    def download_csv_from_s3(self, s3_url: str, job_id: str) -> str:
        """
        Download CSV file from S3 to local temp directory.

        Parameters
        ----------
        s3_url : str
            S3 URL in format: s3://bucket/key or s3://bucket/prefix/file.csv
        job_id : str
            Job ID for unique local file naming

        Returns
        -------
        str
            Local file path of downloaded CSV

        Raises
        ------
        ValueError
            If S3 URL format is invalid
        ClientError
            If S3 download fails
        """
        if not s3_url.startswith("s3://"):
            raise ValueError(f"Invalid S3 URL format: {s3_url}")

        # Parse S3 URL: s3://bucket/key
        s3_url_parts = s3_url.replace("s3://", "").split("/", 1)
        if len(s3_url_parts) != 2:
            raise ValueError(f"Invalid S3 URL format: {s3_url}")

        bucket = s3_url_parts[0]
        key = s3_url_parts[1]

        local_path = os.path.join(self.temp_dir, f"{job_id}_dataset.csv")

        logger.info(f"Downloading s3://{bucket}/{key} to {local_path}")

        try:
            self.s3_client.download_file(bucket, key, local_path)
            logger.info(f"Successfully downloaded CSV: {local_path}")
            return local_path
        except ClientError as e:
            logger.error(f"Failed to download from S3: {e}")
            raise

    def load_and_split_data(
        self, csv_path: str
    ) -> Tuple[pd.DataFrame, pd.DataFrame]:
        """
        Load CSV and split into train/test sets using GroupShuffleSplit.

        Parameters
        ----------
        csv_path : str
            Local path to CSV file

        Returns
        -------
        df_train : pd.DataFrame
            Training data
        df_test : pd.DataFrame
            Test data

        Raises
        ------
        ValueError
            If required columns are missing or data is invalid
        """
        logger.info(f"Loading dataset from {csv_path}")
        df = pd.read_csv(csv_path)
        logger.info(f"Loaded {len(df)} rows from CSV")

        # Validate required columns
        required_cols = ["student_id", "course_grade", "course_hist_median_smooth"]
        for col in required_cols:
            if col not in df.columns:
                raise ValueError(f"Required column '{col}' is missing from dataset")

        # Drop rows with null target or baseline
        mask_valid = (
            df["course_grade"].notna() & 
            df["course_hist_median_smooth"].notna()
        )
        df = df[mask_valid].reset_index(drop=True)
        logger.info(
            f"Rows after dropping NaN in target columns: {len(df)}"
        )

        if len(df) < 100:
            raise ValueError(
                f"Insufficient data after cleaning: {len(df)} rows "
                "(minimum 100 required)"
            )

        # Split by student_id to prevent leakage
        groups = df["student_id"].astype(str)
        gss = GroupShuffleSplit(
            n_splits=1,
            test_size=Config.TEST_SIZE,
            random_state=Config.RANDOM_STATE,
        )
        train_idx, test_idx = next(gss.split(df, groups=groups))

        df_train = df.iloc[train_idx].reset_index(drop=True)
        df_test = df.iloc[test_idx].reset_index(drop=True)

        groups_train = groups.iloc[train_idx]
        groups_test = groups.iloc[test_idx]

        logger.info(f"Train size: {len(df_train)} rows, Test size: {len(df_test)} rows")
        logger.info(
            f"Unique students - Train: {groups_train.nunique()}, "
            f"Test: {groups_test.nunique()}"
        )

        return df_train, df_test

    def cleanup(self, file_path: str):
        """Remove temporary file."""
        try:
            if os.path.exists(file_path):
                os.remove(file_path)
                logger.info(f"Cleaned up temporary file: {file_path}")
        except Exception as e:
            logger.warning(f"Failed to cleanup file {file_path}: {e}")
