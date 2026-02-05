"""
Callback Module
Handles HTTP POST callback to backend service with transcription results.
Groups words into segments (~3s) matching backend logic before sending.
"""

import logging
import time
from typing import Dict, Any, Optional, List

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from .config import Config
from .transcription import TranscriptionResult, Word

logger = logging.getLogger(__name__)


class TranscriptSegment:
    """Segment of transcript with start/end timestamps"""

    def __init__(
        self,
        text: str,
        start_ms: int,
        end_ms: int,
        word_count: int,
        segment_index: int,
    ):
        self.text = text
        self.start_ms = start_ms
        self.end_ms = end_ms
        self.word_count = word_count
        self.segment_index = segment_index


class CallbackHandler:
    """
    Handles sending transcription results back to the backend service.

    Groups words into ~3 second segments (matching TranscriptionServiceImpl.groupWordsIntoSegments())
    and sends each segment separately to preserve precise timestamp information.
    """

    SEGMENT_DURATION_MS = 3000  # 3 seconds per segment (matching backend)
    MAX_SEGMENT_DURATION_MS = 6000  # Force split at 6 seconds (2x)

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

    def send_transcript(
        self,
        callback_url: str,
        lecture_id: str,
        result: TranscriptionResult
    ) -> bool:
        """
        Send transcription result to backend service.
        Groups words into segments and sends ALL segments in ONE batch request.
        """
        logger.info(f"Processing transcript for lecture {lecture_id}: {len(result.words)} words")

        if not result.words:
            logger.warning(f"No words with timestamps for lecture {lecture_id}, cannot segment")
            return False

        # Group words into segments (matching backend TranscriptionServiceImpl logic)
        segments = self._group_words_into_segments(result.words)
        logger.info(f"Created {len(segments)} segments from {len(result.words)} words")

        # Send ALL segments in ONE batch request
        return self._send_batch(
            callback_url=callback_url,
            lecture_id=lecture_id,
            segments=segments,
            language_code=result.language_code,
            total_duration=result.audio_duration,
        )

    def _group_words_into_segments(self, words: List[Word]) -> List[TranscriptSegment]:
        """
        Group words into segments (~3s each) matching TranscriptionServiceImpl.groupWordsIntoSegments().

        Logic (exactly matching backend):
        - Break at sentence boundaries (. ! ?) when duration >= 3s
        - Force break at 6s regardless of punctuation
        """
        if not words:
            return []

        segments = []
        segment_words = []
        segment_start_ms = None
        segment_end_ms = None
        word_count = 0

        for word in words:
            if segment_start_ms is None:
                segment_start_ms = word.start

            segment_words.append(word.text)
            segment_end_ms = word.end
            word_count += 1

            current_duration_ms = segment_end_ms - segment_start_ms
            is_sentence_end = self._is_sentence_end(word.text)

            # Match backend logic: duration >= 3s AND sentence end, OR duration >= 6s (force)
            should_break = (
                (current_duration_ms >= self.SEGMENT_DURATION_MS and is_sentence_end)
                or current_duration_ms >= self.MAX_SEGMENT_DURATION_MS
            )

            if should_break:
                segment = TranscriptSegment(
                    text=" ".join(segment_words),
                    start_ms=segment_start_ms,
                    end_ms=segment_end_ms,
                    word_count=word_count,
                    segment_index=len(segments),
                )
                segments.append(segment)

                # Reset for next segment
                segment_words = []
                segment_start_ms = None
                word_count = 0

        # Add remaining words as final segment
        if segment_words and segment_start_ms is not None:
            segment = TranscriptSegment(
                text=" ".join(segment_words),
                start_ms=segment_start_ms,
                end_ms=segment_end_ms,
                word_count=word_count,
                segment_index=len(segments),
            )
            segments.append(segment)

        return segments

    def _is_sentence_end(self, text: str) -> bool:
        """Check if word ends with sentence punctuation (matching backend regex: .*[.!?]$)"""
        if not text:
            return False
        return text[-1] in ".!?"

    def _send_batch(
        self,
        callback_url: str,
        lecture_id: str,
        segments: List[TranscriptSegment],
        language_code: str,
        total_duration: int,
    ) -> bool:
        """Send ALL segments in ONE batch request matching TranscriptionCallbackBatchRequest structure"""
        payload = {
            "videoLectureId": lecture_id,
            "languageCode": language_code,
            "audioDuration": total_duration,
            "segments": [
                {
                    "transcriptText": segment.text,
                    "startTimeSeconds": segment.start_ms // 1000,  # Convert ms to seconds
                    "endTimeSeconds": segment.end_ms // 1000,
                    "wordCount": segment.word_count,
                    "segmentIndex": segment.segment_index,
                }
                for segment in segments
            ],
        }

        logger.info(f"Sending batch callback to {callback_url} for lecture {lecture_id}: {len(segments)} segments")
        
        # Log the exact payload being sent
        logger.info(f"Callback request body for lecture {lecture_id}:")
        logger.info(f"JSON payload: {payload}")

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

            if response.status_code in [200, 201]:
                logger.info(f"Batch callback successful for lecture {lecture_id}: {len(segments)} segments")
                return True
            else:
                logger.error(f"Batch callback failed for lecture {lecture_id}, "
                            f"status={response.status_code}, response={response.text[:500]}")
                return False

        except requests.exceptions.Timeout:
            logger.error(f"Batch callback timeout for lecture {lecture_id}")
            return False
        except requests.exceptions.ConnectionError as e:
            logger.error(f"Batch callback connection error for lecture {lecture_id}: {e}")
            return False
        except Exception as e:
            logger.error(f"Batch callback error for lecture {lecture_id}: {e}", exc_info=True)
            return False

    def send_error(
        self,
        callback_url: str,
        lecture_id: str,
        error_message: str
    ) -> bool:
        """Send error notification to backend"""
        error_url = callback_url.rstrip('/') + '/error'
        payload = {
            "videoLectureId": lecture_id,
            "error": error_message,
            "status": "FAILED"
        }

        logger.info(f"Sending error notification for lecture {lecture_id}")
        
        # Log the exact error payload being sent
        logger.info(f"Error callback request body for lecture {lecture_id}:")
        logger.info(f"JSON payload: {payload}")

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
        """Close the session"""
        self.session.close()
