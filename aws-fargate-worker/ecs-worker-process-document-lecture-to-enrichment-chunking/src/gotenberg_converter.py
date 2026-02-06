"""
Gotenberg Converter Module
Converts DOCX, PPTX, and other formats to PDF using Gotenberg API.
"""

import logging
import os
from typing import Optional

import requests

from .config import Config

logger = logging.getLogger(__name__)


class GotenbergConverter:
    """
    Converts various document formats to PDF using Gotenberg.
    Gotenberg runs as a sidecar container exposing port 3000.
    """

    # Formats that need conversion
    CONVERTIBLE_FORMATS = ['docx', 'doc', 'pptx', 'ppt', 'xlsx', 'xls', 'odt', 'odp', 'ods']

    def __init__(self):
        self.gotenberg_url = Config.GOTENBERG_URL
        self.timeout = Config.GOTENBERG_TIMEOUT
        self.session = requests.Session()
        logger.info(f"GotenbergConverter initialized with URL: {self.gotenberg_url}")

    def needs_conversion(self, file_path: str) -> bool:
        """Check if file needs to be converted to PDF."""
        ext = self._get_extension(file_path)
        return ext in self.CONVERTIBLE_FORMATS

    def convert_to_pdf(self, file_path: str, lecture_id: str) -> Optional[str]:
        """
        Convert document to PDF using Gotenberg.

        Args:
            file_path: Path to input file
            lecture_id: Lecture ID for output file naming

        Returns:
            Path to converted PDF, or None if failed
        """
        ext = self._get_extension(file_path)

        if ext == 'pdf':
            logger.info(f"File is already PDF: {file_path}")
            return file_path

        if ext not in self.CONVERTIBLE_FORMATS:
            logger.warning(f"Unsupported format for conversion: {ext}")
            return None

        logger.info(f"Converting {ext.upper()} to PDF for lecture {lecture_id}")

        try:
            # Check Gotenberg health before conversion
            if not self.is_healthy():
                logger.error("Gotenberg service is not healthy, cannot convert document")
                return None

            # Prepare output path
            output_path = os.path.join(Config.TEMP_DIR, f"{lecture_id}_converted.pdf")

            # Call Gotenberg LibreOffice convert endpoint
            convert_url = f"{self.gotenberg_url}/forms/libreoffice/convert"

            with open(file_path, 'rb') as f:
                files = {
                    'files': (os.path.basename(file_path), f)
                }

                response = self.session.post(
                    convert_url,
                    files=files,
                    timeout=self.timeout
                )

            if response.status_code != 200:
                logger.error(f"Gotenberg conversion failed: {response.status_code} - {response.text[:500]}")
                return None

            # Save converted PDF
            with open(output_path, 'wb') as f:
                f.write(response.content)

            file_size = os.path.getsize(output_path)
            logger.info(f"Converted to PDF: {output_path}, size: {file_size} bytes")

            return output_path

        except requests.exceptions.Timeout:
            logger.error(f"Gotenberg conversion timeout for lecture {lecture_id}")
            return None
        except requests.exceptions.ConnectionError as e:
            logger.error(f"Cannot connect to Gotenberg: {e}")
            return None
        except Exception as e:
            logger.error(f"Gotenberg conversion error: {e}", exc_info=True)
            return None

    def is_healthy(self) -> bool:
        """Check if Gotenberg service is healthy."""
        try:
            response = self.session.get(
                f"{self.gotenberg_url}/health",
                timeout=10
            )
            return response.status_code == 200
        except Exception as e:
            logger.warning(f"Gotenberg health check failed: {e}")
            return False

    def _get_extension(self, file_path: str) -> str:
        """Get file extension in lowercase without dot."""
        return os.path.splitext(file_path)[1].lower().lstrip('.')
