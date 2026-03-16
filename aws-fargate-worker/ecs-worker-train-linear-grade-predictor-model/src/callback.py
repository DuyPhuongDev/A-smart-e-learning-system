"""
Callback Module
Handles HTTP POST callback to backend service with training results.
"""

import logging
from typing import Dict, Any

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from .config import Config

logger = logging.getLogger(__name__)


class CallbackHandler:
    """
    Handles sending training results back to the backend service.
    """

    def __init__(self):
        self.timeout = Config.CALLBACK_TIMEOUT
        self.max_retries = Config.CALLBACK_RETRIES
        self.callback_url = Config.CALLBACK_URL_PLACEHOLDER

        # Setup session with retry strategy
        self.session = requests.Session()
        retry_strategy = Retry(
            total=self.max_retries,
            backoff_factor=1,
            status_forcelist=[429, 500, 502, 503, 504],
            allowed_methods=["POST"]
        )
        adapter = HTTPAdapter(max_retries=retry_strategy)
        self.session.mount("http://", adapter)
        self.session.mount("https://", adapter)

        logger.info(
            f"CallbackHandler initialized with timeout={self.timeout}s, "
            f"retries={self.max_retries}, url={self.callback_url}"
        )

    def send_training_result(
        self,
        job_id: str,
        model_name: str,
        model_s3_path: str,
        metrics: Dict[str, Any],
        training_time_seconds: float,
        status: str = "SUCCESS"
    ) -> bool:
        """
        Send training result to backend service.

        Parameters
        ----------
        job_id : str
            Training job ID from SQS message
        model_name : str
            Model name from SQS message
        model_s3_path : str
            Full S3 path where model was saved (s3://bucket/key)
        metrics : dict
            Metrics dictionary with train/test metrics (4-point scale)
        training_time_seconds : float
            Total training time in seconds
        status : str
            Job status: "SUCCESS" or "FAILED"

        Returns
        -------
        bool
            True if callback successful, False otherwise
        """
        payload = {
            "jobId": job_id,
            "modelName": model_name,
            "modelS3Path": model_s3_path,
            "status": status,
            "trainingTimeSeconds": training_time_seconds,
            "metrics": metrics,  # Metrics on 4-point scale
        }

        logger.info(f"Sending training result callback for job {job_id}")
        logger.debug(f"Payload: {payload}")

        try:
            response = self.session.post(
                self.callback_url,
                json=payload,
                headers={
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                timeout=self.timeout
            )

            if response.status_code in [200, 201]:
                logger.info(f"Callback successful for job {job_id}")
                return True
            else:
                logger.error(
                    f"Callback failed for job {job_id}, "
                    f"status={response.status_code}, body={response.text}"
                )
                return False

        except requests.exceptions.Timeout:
            logger.error(f"Callback timeout for job {job_id}")
            return False
        except requests.exceptions.ConnectionError as e:
            logger.error(f"Callback connection error for job {job_id}: {e}")
            return False
        except Exception as e:
            logger.error(f"Callback error for job {job_id}: {e}", exc_info=True)
            return False

    def send_error(
        self,
        job_id: str,
        model_name: str,
        error_message: str
    ) -> bool:
        """
        Send error notification to backend.

        Parameters
        ----------
        job_id : str
            Training job ID
        model_name : str
            Model name from message
        error_message : str
            Error description

        Returns
        -------
        bool
            True if callback successful
        """
        error_url = self.callback_url.rstrip('/') + '/error'
        payload = {
            "jobId": job_id,
            "modelName": model_name,
            "status": "FAILED",
            "error": error_message,
        }

        try:
            response = self.session.post(
                error_url,
                json=payload,
                headers={'Content-Type': 'application/json'},
                timeout=self.timeout
            )
            # Accept 200, 201, or even 404 (endpoint might not exist yet)
            return response.status_code in [200, 201, 404]
        except Exception as e:
            logger.warning(f"Could not send error notification: {e}")
            return False

    def close(self):
        """Close the session"""
        self.session.close()
