"""
Callback Module
Handles HTTP POST callback to backend service with enrichment results.
"""

import logging
from typing import List, Dict, Any

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from .config import Config
from .llm_enricher import EnrichedChunk

logger = logging.getLogger(__name__)


class CallbackHandler:
    """
    Handles sending enrichment results back to the backend service.
    Sends all chunks in ONE batch request.
    """

    def __init__(self):
        self.timeout = Config.CALLBACK_TIMEOUT
        self.max_retries = Config.CALLBACK_RETRIES

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

        logger.info(f"CallbackHandler initialized with timeout={self.timeout}s, retries={self.max_retries}")

    def send_enrichment_result(
        self,
        callback_url: str,
        lecture_id: str,
        content_type: str,
        lecture_title: str,
        chunks: List[EnrichedChunk]
    ) -> bool:
        """
        Send enrichment results to backend service.
        Sends ALL chunks in ONE batch request.

        Args:
            callback_url: URL to send results to
            lecture_id: Lecture ID
            content_type: "DOCUMENT" or "TEXT"
            lecture_title: Title of the lecture
            chunks: List of enriched chunks

        Returns:
            True if successful, False otherwise
        """
        logger.info(f"Sending enrichment callback for lecture {lecture_id}: {len(chunks)} chunks")

        payload = self._build_payload(lecture_id, content_type, lecture_title, chunks)

        # Log the payload
        logger.info(f"Callback request body for lecture {lecture_id}:")
        logger.debug(f"JSON payload: {payload}")

        try:
            response = self.session.post(
                callback_url,
                json=payload,
                headers={
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                timeout=self.timeout
            )

            # Accept 200, 201, 202 as success
            if response.status_code in [200, 201, 202]:
                logger.info(f"Callback successful for lecture {lecture_id}: {len(chunks)} chunks, status={response.status_code}")
                return True
            else:
                response_text = response.text[:500] if response.text else 'No response body'
                logger.error(f"Callback failed for lecture {lecture_id}, "
                            f"status={response.status_code}, response={response_text}")
                return False

        except requests.exceptions.Timeout:
            logger.error(f"Callback timeout for lecture {lecture_id}")
            return False
        except requests.exceptions.ConnectionError as e:
            logger.error(f"Callback connection error for lecture {lecture_id}: {e}")
            return False
        except Exception as e:
            logger.error(f"Callback error for lecture {lecture_id}: {e}", exc_info=True)
            return False

    def _build_payload(
        self,
        lecture_id: str,
        content_type: str,
        lecture_title: str,
        chunks: List[EnrichedChunk]
    ) -> Dict[str, Any]:
        """Build callback payload matching DocumentEnrichmentCallbackRequest."""
        return {
            "lectureId": lecture_id,
            "contentType": content_type,
            "lectureTitle": lecture_title,
            "totalChunks": len(chunks),
            "chunks": [
                {
                    "chunkIndex": chunk.chunk_index,
                    "chunkContent": chunk.enriched_content,
                    "originalText": chunk.original_text,
                    "summary": chunk.summary,
                    "questions": chunk.questions,
                    "pageNumber": chunk.page_number,
                    "startPage": chunk.start_page,
                    "endPage": chunk.end_page,
                    "tokenCount": chunk.token_count,
                    "isEnriched": chunk.is_enriched
                }
                for chunk in chunks
            ]
        }

    def send_error(
        self,
        callback_url: str,
        lecture_id: str,
        error_message: str
    ) -> bool:
        """Send error notification to backend."""
        error_url = callback_url.rstrip('/') + '/error'
        payload = {
            "lectureId": lecture_id,
            "error": error_message,
            "status": "FAILED"
        }

        logger.info(f"Sending error notification for lecture {lecture_id}")

        try:
            response = self.session.post(
                error_url,
                json=payload,
                headers={'Content-Type': 'application/json'},
                timeout=self.timeout
            )
            return response.status_code in [200, 201, 404]
        except Exception as e:
            logger.warning(f"Could not send error notification: {e}")
            return False

    def close(self):
        """Close the session."""
        self.session.close()
