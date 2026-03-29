"""
AssemblyAI Transcription Handler
Uploads audio directly to AssemblyAI and polls until completion.
Extracts word-level timestamps for precise segmentation.
"""

import logging
import time
from typing import Optional, Dict, Any, List

import requests

from .config import Config

logger = logging.getLogger(__name__)


class Word:
    """Word with timestamp information from AssemblyAI"""

    def __init__(self, text: str, start: int, end: int, confidence: float):
        self.text = text
        self.start = start  # milliseconds
        self.end = end  # milliseconds
        self.confidence = confidence

    def to_dict(self) -> Dict[str, Any]:
        return {
            "text": self.text,
            "start": self.start,
            "end": self.end,
            "confidence": self.confidence,
        }


class TranscriptionResult:
    """Result object containing transcription data with word-level timestamps"""

    def __init__(
        self,
        transcript_text: str,
        language_code: str,
        language_confidence: float,
        audio_duration: int,
        word_count: int,
        words: List[Word],
    ):
        self.transcript_text = transcript_text
        self.language_code = language_code
        self.language_confidence = language_confidence
        self.audio_duration = audio_duration
        self.word_count = word_count
        self.words = words

    def to_dict(self) -> Dict[str, Any]:
        return {
            "transcript_text": self.transcript_text,
            "language_code": self.language_code,
            "language_confidence": self.language_confidence,
            "audio_duration": self.audio_duration,
            "word_count": self.word_count,
            "words": [w.to_dict() for w in self.words],
        }


class TranscriptionHandler:
    """
    Handles AssemblyAI operations:
    1. Upload audio file
    2. Start transcription with auto language detection
    3. Poll until completion
    4. Parse result
    """

    BASE_URL = "https://api.assemblyai.com/v2"
    UPLOAD_ENDPOINT = "/upload"
    TRANSCRIPT_ENDPOINT = "/transcript"
    POLL_INTERVAL_SEC = 5
    MAX_POLL_ATTEMPTS = 120  # ~10 minutes

    def __init__(self):
        self.api_key = Config.ASSEMBLYAI_API_KEY
        if not self.api_key:
            logger.warning("ASSEMBLYAI_API_KEY is not set; transcription will fail.")
        self.session = requests.Session()
        self.session.headers.update({"Authorization": self.api_key})

    def transcribe_audio(self, audio_file_path: str, lecture_id: str) -> Optional[TranscriptionResult]:
        """Upload and transcribe using AssemblyAI."""
        try:
            upload_url = self._upload_file(audio_file_path)
            if not upload_url:
                return None

            transcript_id = self._start_transcription(upload_url)
            if not transcript_id:
                return None

            transcript = self._poll_transcript(transcript_id)
            if not transcript:
                return None

            result = self._parse_transcript(transcript)
            logger.info(
                "Transcription completed for lecture %s: language=%s, duration=%ss, words=%s",
                lecture_id,
                result.language_code,
                result.audio_duration,
                result.word_count,
            )
            return result
        except Exception as e:
            logger.error("Transcription failed for lecture %s: %s", lecture_id, e, exc_info=True)
            return None

    def _upload_file(self, file_path: str) -> Optional[str]:
        logger.info("Uploading audio to AssemblyAI: %s", file_path)
        try:
            with open(file_path, "rb") as f:
                resp = self.session.post(self.BASE_URL + self.UPLOAD_ENDPOINT, data=f)
            if resp.ok:
                upload_url = resp.json().get("upload_url")
                logger.info("Upload succeeded")
                return upload_url
            logger.error("Upload failed: %s %s", resp.status_code, resp.text)
            return None
        except Exception as e:
            logger.error("Upload error: %s", e, exc_info=True)
            return None

    def _start_transcription(self, upload_url: str) -> Optional[str]:
        payload = {
            "audio_url": upload_url,
            "language_detection": True,
            "speaker_labels": False,
            "punctuate": True,
            "format_text": True,
        }
        try:
            resp = self.session.post(self.BASE_URL + self.TRANSCRIPT_ENDPOINT, json=payload)
            if resp.ok:
                tid = resp.json().get("id")
                logger.info("Transcription started: %s", tid)
                return tid
            logger.error("Start transcription failed: %s %s", resp.status_code, resp.text)
            return None
        except Exception as e:
            logger.error("Start transcription error: %s", e, exc_info=True)
            return None

    def _poll_transcript(self, transcript_id: str) -> Optional[Dict[str, Any]]:
        url = f"{self.BASE_URL}{self.TRANSCRIPT_ENDPOINT}/{transcript_id}"
        for attempt in range(self.MAX_POLL_ATTEMPTS):
            try:
                resp = self.session.get(url)
                if not resp.ok:
                    logger.error("Poll failed: %s %s", resp.status_code, resp.text)
                    return None
                data = resp.json()
                status = data.get("status")
                if status == "completed":
                    logger.info("Transcription completed: %s", transcript_id)
                    return data
                if status == "error":
                    logger.error("Transcription error: %s", data.get("error"))
                    return None
                time.sleep(self.POLL_INTERVAL_SEC)
            except Exception as e:
                logger.error("Poll error: %s", e, exc_info=True)
                return None
        logger.error("Transcription polling timed out")
        return None

    def _parse_transcript(self, data: Dict[str, Any]) -> TranscriptionResult:
        transcript_text = data.get("text") or ""
        language_code = data.get("language_code") or "en"
        language_confidence = data.get("language_confidence") or 0.0
        audio_duration = int(data.get("audio_duration") or 0)
        word_count = len(transcript_text.split()) if transcript_text else 0

        # Extract word-level timestamps
        words_data = data.get("words") or []
        words = []
        for w in words_data:
            word = Word(
                text=w.get("text", ""),
                start=w.get("start", 0),
                end=w.get("end", 0),
                confidence=w.get("confidence", 0.0),
            )
            words.append(word)

        logger.info("Parsed transcript: %d words with timestamps", len(words))

        return TranscriptionResult(
            transcript_text=transcript_text,
            language_code=language_code,
            language_confidence=float(language_confidence),
            audio_duration=audio_duration,
            word_count=word_count,
            words=words,
        )
