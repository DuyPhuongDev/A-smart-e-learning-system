"""
Video Downloader Module
Handles downloading videos from S3/CloudFront (direct URL) and YouTube
Extracts audio in optimal format for AWS Transcribe (mp3/m4a)
"""

import os
import subprocess
import shutil
import urllib.request
import logging
from typing import Optional
from pathlib import Path

from .config import Config

logger = logging.getLogger(__name__)


class VideoDownloader:
    """
    Downloads video/audio from S3/CloudFront or YouTube and extracts audio for transcription.

    Strategies:
    - YouTube: Uses yt-dlp to download audio directly (m4a/mp3) - most efficient
    - S3/CloudFront: Downloads file directly via HTTP, extracts audio using ffmpeg if needed

    Note: CloudFront URLs (e.g., https://d14s8phoypchnb.cloudfront.net/videos/xxx.mkv)
    are public URLs, NOT presigned URLs. They can be downloaded directly.
    """

    # Supported video extensions that need audio extraction
    VIDEO_EXTENSIONS = {'.mp4', '.mkv', '.avi', '.mov', '.wmv', '.flv', '.webm', '.m4v'}

    # Audio extensions that can be used directly
    AUDIO_EXTENSIONS = {'.mp3', '.m4a', '.wav', '.flac', '.aac', '.ogg'}

    def __init__(self):
        self.temp_dir = Path(Config.TEMP_DIR)
        self.temp_dir.mkdir(parents=True, exist_ok=True)
        logger.info(f"VideoDownloader initialized with temp_dir: {self.temp_dir}")

    def download_and_extract_audio(
        self,
        video_url: str,
        source_type: str,
        lecture_id: str
    ) -> Optional[str]:
        """
        Download video/audio and extract audio file for transcription.

        Args:
            video_url: CloudFront URL, S3 presigned URL, or YouTube URL
            source_type: 's3', 'cloudfront', 'direct', or 'youtube'
            lecture_id: Unique lecture ID for file naming

        Returns:
            Path to the audio file (mp3/m4a) or None if failed
        """
        logger.info(f"Starting download for lecture {lecture_id}, source: {source_type}, url: {video_url[:100]}...")

        try:
            if source_type.lower() == 'youtube':
                return self._download_youtube_audio(video_url, lecture_id)
            elif source_type.lower() in ('s3', 'cloudfront', 'direct'):
                # All these types are downloaded via HTTP directly
                return self._download_http_and_extract(video_url, lecture_id)
            else:
                logger.error(f"Unsupported source type: {source_type}")
                return None
        except Exception as e:
            logger.error(f"Failed to download/extract audio: {e}", exc_info=True)
            return None

    def _download_youtube_audio(self, youtube_url: str, lecture_id: str) -> Optional[str]:
        """
        Download audio directly from YouTube using yt-dlp.
        This is the most efficient method - downloads only audio stream.

        Output format: mp3 (supported by AWS Transcribe)
        """
        logger.info(f"Downloading YouTube audio: {youtube_url}")

        output_path = self.temp_dir / f"{lecture_id}_audio.%(ext)s"
        final_path = self.temp_dir / f"{lecture_id}_audio.mp3"

        # yt-dlp command to download best audio and convert to mp3
        cmd = [
            'yt-dlp',
            '-f', 'bestaudio[ext=m4a]/bestaudio/best',  # Best audio quality
            '-x',  # Extract audio
            '--audio-format', 'mp3',  # Convert to mp3
            '--audio-quality', '192K',  # Good quality, reasonable size
            '-o', str(output_path),
            '--no-playlist',  # Don't download playlists
            '--socket-timeout', '60',
            '--retries', '3',
            youtube_url
        ]

        logger.info(f"Running yt-dlp command: {' '.join(cmd)}")

        try:
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=600  # 10 minutes timeout
            )

            if result.returncode != 0:
                logger.error(f"yt-dlp failed: {result.stderr}")
                return None

            # Find the downloaded file (extension might vary)
            for ext in ['.mp3', '.m4a', '.wav', '.opus', '.webm']:
                potential_path = self.temp_dir / f"{lecture_id}_audio{ext}"
                if potential_path.exists():
                    logger.info(f"YouTube audio downloaded: {potential_path}, size: {potential_path.stat().st_size} bytes")
                    return str(potential_path)

            # Check if mp3 exists
            if final_path.exists():
                logger.info(f"YouTube audio downloaded: {final_path}, size: {final_path.stat().st_size} bytes")
                return str(final_path)

            logger.error("Downloaded file not found after yt-dlp completed")
            return None

        except subprocess.TimeoutExpired:
            logger.error("yt-dlp download timed out")
            return None
        except Exception as e:
            logger.error(f"yt-dlp error: {e}", exc_info=True)
            return None

    def _download_http_and_extract(self, url: str, lecture_id: str) -> Optional[str]:
        """
        Download file from HTTP URL (CloudFront, S3 presigned, or direct) and extract audio if needed.

        CloudFront URLs like https://d14s8phoypchnb.cloudfront.net/videos/xxx.mkv
        are public and can be downloaded directly without authentication.
        """
        logger.info(f"Downloading from HTTP URL: {url[:100]}...")

        # Determine file extension from URL
        url_path = url.split('?')[0]  # Remove query params (for presigned URLs)
        ext = Path(url_path).suffix.lower() or '.mp4'

        download_path = self.temp_dir / f"{lecture_id}_video{ext}"
        audio_path = self.temp_dir / f"{lecture_id}_audio.mp3"

        try:
            # Download file with custom headers
            logger.info(f"Downloading to: {download_path}")

            # Create request with headers to avoid some server restrictions
            request = urllib.request.Request(
                url,
                headers={
                    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
                    'Accept': '*/*'
                }
            )

            with urllib.request.urlopen(request, timeout=300) as response:
                with open(download_path, 'wb') as out_file:
                    shutil.copyfileobj(response, out_file)

            file_size = download_path.stat().st_size
            logger.info(f"Downloaded file size: {file_size} bytes")

            if file_size == 0:
                logger.error("Downloaded file is empty")
                return None

            # Check if it's already an audio file
            if ext in self.AUDIO_EXTENSIONS:
                logger.info("File is already audio, using directly")
                # If it's already mp3, just return it
                if ext == '.mp3':
                    return str(download_path)
                # Otherwise convert to mp3 for consistency
                return self._convert_to_mp3(download_path, audio_path)

            # Extract audio from video using ffmpeg
            return self._extract_audio_ffmpeg(download_path, audio_path)

        except urllib.error.HTTPError as e:
            logger.error(f"HTTP error downloading file: {e.code} {e.reason}")
            return None
        except urllib.error.URLError as e:
            logger.error(f"URL error downloading file: {e.reason}")
            return None
        except Exception as e:
            logger.error(f"HTTP download/extract failed: {e}", exc_info=True)
            return None
        finally:
            # Clean up video file if audio was extracted
            if download_path.exists() and audio_path.exists():
                try:
                    download_path.unlink()
                    logger.info(f"Cleaned up video file: {download_path}")
                except Exception as e:
                    logger.warning(f"Failed to clean up video file: {e}")

    def _extract_audio_ffmpeg(self, video_path: Path, audio_path: Path) -> Optional[str]:
        """
        Extract audio from video using ffmpeg.
        Output: MP3 format (well supported by AWS Transcribe)
        """
        logger.info(f"Extracting audio from {video_path} to {audio_path}")

        cmd = [
            'ffmpeg',
            '-i', str(video_path),
            '-vn',  # No video
            '-acodec', 'libmp3lame',  # MP3 codec
            '-q:a', '2',  # High quality VBR
            '-y',  # Overwrite output
            str(audio_path)
        ]

        try:
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=600  # 10 minutes
            )

            if result.returncode != 0:
                logger.error(f"ffmpeg failed: {result.stderr}")
                return None

            if audio_path.exists():
                logger.info(f"Audio extracted: {audio_path}, size: {audio_path.stat().st_size} bytes")
                return str(audio_path)

            logger.error("Audio file not found after ffmpeg completed")
            return None

        except subprocess.TimeoutExpired:
            logger.error("ffmpeg extraction timed out")
            return None
        except Exception as e:
            logger.error(f"ffmpeg error: {e}", exc_info=True)
            return None

    def _convert_to_mp3(self, input_path: Path, output_path: Path) -> Optional[str]:
        """Convert audio file to MP3 format"""
        logger.info(f"Converting {input_path} to MP3")

        cmd = [
            'ffmpeg',
            '-i', str(input_path),
            '-acodec', 'libmp3lame',
            '-q:a', '2',
            '-y',
            str(output_path)
        ]

        try:
            result = subprocess.run(cmd, capture_output=True, text=True, timeout=300)

            if result.returncode == 0 and output_path.exists():
                logger.info(f"Converted to MP3: {output_path}")
                return str(output_path)

            logger.error(f"Conversion failed: {result.stderr}")
            return None

        except Exception as e:
            logger.error(f"Conversion error: {e}", exc_info=True)
            return None

    def cleanup(self, file_path: str):
        """Clean up temporary file"""
        try:
            if file_path and os.path.exists(file_path):
                os.remove(file_path)
                logger.info(f"Cleaned up: {file_path}")
        except Exception as e:
            logger.warning(f"Failed to cleanup {file_path}: {e}")

    def cleanup_all(self):
        """Clean up all temporary files in temp directory"""
        try:
            for file in self.temp_dir.iterdir():
                if file.is_file():
                    file.unlink()
            logger.info("Cleaned up all temp files")
        except Exception as e:
            logger.warning(f"Failed to cleanup temp directory: {e}")
