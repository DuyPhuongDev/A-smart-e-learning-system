"""
Document Downloader Module
Downloads documents from S3/CloudFront pre-signed URLs.
"""

import logging
import os
import shutil
from typing import Optional
from urllib.parse import urlparse

import requests

from .config import Config

logger = logging.getLogger(__name__)


class DocumentDownloader:
    """
    Handles downloading documents from various sources:
    - S3 pre-signed URLs
    - CloudFront URLs
    - Direct HTTP URLs
    """

    def __init__(self):
        self.temp_dir = Config.TEMP_DIR
        os.makedirs(self.temp_dir, exist_ok=True)
        self.session = requests.Session()
        logger.info(f"DocumentDownloader initialized with temp_dir: {self.temp_dir}")

    def download_document(
        self,
        download_url: str,
        lecture_id: str,
        file_format: Optional[str] = None
    ) -> Optional[str]:
        """
        Download document from URL.

        Args:
            download_url: URL to download from
            lecture_id: Lecture ID for file naming
            file_format: Optional file format hint (pdf, docx, pptx)

        Returns:
            Path to downloaded file, or None if failed
        """
        logger.info(f"Downloading document for lecture {lecture_id}")

        try:
            # Determine file extension
            extension = self._get_file_extension(download_url, file_format)
            local_path = os.path.join(self.temp_dir, f"{lecture_id}.{extension}")

            # Download file
            response = self.session.get(
                download_url,
                stream=True,
                timeout=300  # 5 minutes timeout
            )
            response.raise_for_status()

            # Save to local file
            with open(local_path, 'wb') as f:
                for chunk in response.iter_content(chunk_size=8192):
                    f.write(chunk)

            file_size = os.path.getsize(local_path)
            logger.info(f"Downloaded document to {local_path}, size: {file_size} bytes")

            # Validate file was actually downloaded
            if file_size == 0:
                logger.error(f"Downloaded file is empty: {local_path}")
                self.cleanup(local_path)
                return None

            return local_path

        except requests.exceptions.Timeout:
            logger.error(f"Timeout downloading document for lecture {lecture_id}")
            return None
        except requests.exceptions.RequestException as e:
            logger.error(f"Failed to download document: {e}")
            return None
        except IOError as e:
            logger.error(f"IO error saving document: {e}")
            return None
        except Exception as e:
            logger.error(f"Unexpected error downloading document: {e}")
            return None

    def _get_file_extension(self, url: str, file_format: Optional[str]) -> str:
        """Determine file extension from URL or format hint."""
        if file_format:
            return file_format.lower().lstrip('.')

        # Try to extract from URL
        parsed = urlparse(url)
        path = parsed.path.lower()

        for ext in ['pdf', 'docx', 'pptx', 'doc', 'ppt', 'xlsx', 'xls']:
            if path.endswith(f'.{ext}'):
                return ext

        # Default to pdf
        return 'pdf'

    def cleanup(self, file_path: str):
        """Remove a specific file."""
        try:
            if file_path and os.path.exists(file_path):
                os.remove(file_path)
                logger.debug(f"Cleaned up file: {file_path}")
        except Exception as e:
            logger.warning(f"Failed to cleanup file {file_path}: {e}")

    def cleanup_all(self):
        """Remove all files in temp directory."""
        try:
            if os.path.exists(self.temp_dir):
                shutil.rmtree(self.temp_dir)
                os.makedirs(self.temp_dir, exist_ok=True)
                logger.info("Cleaned up all temp files")
        except Exception as e:
            logger.warning(f"Failed to cleanup temp directory: {e}")
