"""
Main Entry Point for ECS Worker
Polls SQS queue for video transcription jobs, processes them, and sends results back.

Features:
- Long polling SQS for efficiency
- Automatic exit after MAX_EMPTY_POLLS consecutive empty polls (cost optimization)
- Error handling with SQS retry mechanism (messages go to DLQ after max retries)
- Cleanup of temporary files
"""

import json
import logging
import sys
import os
import signal
import time
from typing import Optional, Dict, Any

import boto3
from botocore.exceptions import ClientError

from .config import Config
from .video_downloader import VideoDownloader
from .transcription import TranscriptionHandler
from .callback import CallbackHandler

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

# Global flag for graceful shutdown
shutdown_requested = False


def signal_handler(signum, frame):
    """Handle shutdown signals gracefully"""
    global shutdown_requested
    logger.info(f"Received signal {signum}, initiating graceful shutdown...")
    shutdown_requested = True


class SQSMessage:
    """Parsed SQS message containing job details"""

    def __init__(self, raw_message: Dict):
        self.receipt_handle = raw_message['ReceiptHandle']
        self.message_id = raw_message['MessageId']

        # Parse message body
        body = json.loads(raw_message['Body'])

        self.lecture_id: str = body['lectureId']
        self.video_url: str = body['videoUrl']
        self.source_type: str = body['sourceType']  # 's3' or 'youtube'
        self.callback_url: str = body['callbackUrl']
        self.lecture_title: str = body.get('lectureTitle', 'Unknown')

    def __str__(self):
        return (f"SQSMessage(id={self.message_id}, lecture={self.lecture_id}, "
                f"source={self.source_type})")


class Worker:
    """
    Main worker class that orchestrates the video transcription pipeline:
    1. Poll SQS for messages
    2. Download video/audio
    3. Transcribe using AssemblyAI
    4. Send results to backend via callback
    5. Delete message from SQS on success
    """

    def __init__(self):
        logger.info("Initializing Worker...")

        # Validate configuration
        Config.validate()
        Config.print_config()

        # Initialize AWS SQS client
        self.sqs_client = boto3.client('sqs', region_name=Config.AWS_REGION)
        self.queue_url = Config.SQS_QUEUE_URL

        # Initialize handlers
        self.video_downloader = VideoDownloader()
        self.transcription_handler = TranscriptionHandler()
        self.callback_handler = CallbackHandler()

        # Worker state
        self.empty_poll_count = 0
        self.processed_count = 0
        self.failed_count = 0

        logger.info("Worker initialized successfully")

    def run(self):
        """Main worker loop"""
        logger.info("Starting worker loop...")

        while not shutdown_requested:
            try:
                # Poll for messages
                message = self._poll_message()

                if message is None:
                    # No message received
                    self.empty_poll_count += 1
                    logger.info(f"No messages received. Empty poll count: {self.empty_poll_count}/{Config.MAX_EMPTY_POLLS}")

                    # Exit if too many empty polls (Fargate cost optimization)
                    if self.empty_poll_count >= Config.MAX_EMPTY_POLLS:
                        logger.info(f"Reached MAX_EMPTY_POLLS ({Config.MAX_EMPTY_POLLS}). Exiting worker.")
                        break

                    continue

                # Reset empty poll counter on successful message receive
                self.empty_poll_count = 0

                # Process the message
                success = self._process_message(message)

                if success:
                    # Delete message from queue on success
                    self._delete_message(message)
                    self.processed_count += 1
                    logger.info(f"Successfully processed lecture {message.lecture_id}. "
                               f"Total processed: {self.processed_count}")
                else:
                    # Don't delete - SQS will retry after visibility timeout
                    # After maxReceiveCount retries, message goes to DLQ
                    self.failed_count += 1
                    logger.error(f"Failed to process lecture {message.lecture_id}. "
                                f"Message will be retried. Total failures: {self.failed_count}")

            except KeyboardInterrupt:
                logger.info("Keyboard interrupt received. Shutting down...")
                break
            except Exception as e:
                logger.error(f"Unexpected error in worker loop: {e}", exc_info=True)
                # Continue processing - don't crash on unexpected errors
                time.sleep(5)

        self._cleanup()
        logger.info(f"Worker stopped. Processed: {self.processed_count}, Failed: {self.failed_count}")

    def _poll_message(self) -> Optional[SQSMessage]:
        """Poll SQS for a single message using long polling"""
        try:
            response = self.sqs_client.receive_message(
                QueueUrl=self.queue_url,
                MaxNumberOfMessages=1,
                WaitTimeSeconds=Config.SQS_WAIT_TIME_SECONDS,
                VisibilityTimeout=Config.SQS_VISIBILITY_TIMEOUT,
                AttributeNames=['All'],
                MessageAttributeNames=['All']
            )

            messages = response.get('Messages', [])

            if not messages:
                return None

            raw_message = messages[0]
            message = SQSMessage(raw_message)
            logger.info(f"Received message: {message}")

            return message

        except ClientError as e:
            logger.error(f"SQS receive error: {e}")
            return None
        except json.JSONDecodeError as e:
            logger.error(f"Failed to parse message body: {e}")
            return None
        except KeyError as e:
            logger.error(f"Message missing required field: {e}")
            return None

    def _process_message(self, message: SQSMessage) -> bool:
        """
        Process a single transcription job.

        Pipeline:
        1. Download video/audio from S3/CloudFront or YouTube
        2. Transcribe using AssemblyAI (auto language detection)
        3. Send results to backend via callback
        """
        logger.info(f"Processing lecture: {message.lecture_id} ({message.source_type})")

        audio_file_path = None

        try:
            # Step 1: Download and extract audio
            logger.info(f"Step 1: Downloading from {message.source_type}...")
            audio_file_path = self.video_downloader.download_and_extract_audio(
                video_url=message.video_url,
                source_type=message.source_type,
                lecture_id=message.lecture_id
            )

            if not audio_file_path:
                logger.error(f"Failed to download/extract audio for lecture {message.lecture_id}")
                return False

            logger.info(f"Audio ready: {audio_file_path}")

            # Step 2: Transcribe using AssemblyAI
            logger.info("Step 2: Transcribing with AssemblyAI...")
            result = self.transcription_handler.transcribe_audio(
                audio_file_path=audio_file_path,
                lecture_id=message.lecture_id
            )

            if not result:
                logger.error(f"Transcription failed for lecture {message.lecture_id}")
                return False

            logger.info(f"Transcription complete: {result.word_count} words, "
                       f"language={result.language_code}, duration={result.audio_duration}s")

            # Step 3: Send results to backend
            logger.info("Step 3: Sending results to backend...")
            callback_success = self.callback_handler.send_transcript(
                callback_url=message.callback_url,
                lecture_id=message.lecture_id,
                result=result
            )

            if not callback_success:
                logger.error(f"Callback failed for lecture {message.lecture_id}")
                return False

            logger.info(f"Successfully processed lecture {message.lecture_id}")
            return True

        except Exception as e:
            logger.error(f"Error processing message: {e}", exc_info=True)
            return False

        finally:
            # Cleanup local audio file
            if audio_file_path:
                self.video_downloader.cleanup(audio_file_path)

    def _delete_message(self, message: SQSMessage):
        """Delete message from SQS after successful processing"""
        try:
            self.sqs_client.delete_message(
                QueueUrl=self.queue_url,
                ReceiptHandle=message.receipt_handle
            )
            logger.info(f"Deleted message {message.message_id} from queue")
        except ClientError as e:
            logger.error(f"Failed to delete message: {e}")

    def _cleanup(self):
        """Cleanup resources before shutdown"""
        logger.info("Cleaning up resources...")

        try:
            self.video_downloader.cleanup_all()
            self.callback_handler.close()
        except Exception as e:
            logger.warning(f"Cleanup error: {e}")


def main():
    """Main entry point"""
    # Register signal handlers for graceful shutdown
    signal.signal(signal.SIGTERM, signal_handler)
    signal.signal(signal.SIGINT, signal_handler)

    logger.info("=" * 60)
    logger.info("ECS Worker for Video Transcription - Starting")
    logger.info("=" * 60)

    try:
        worker = Worker()
        worker.run()

        logger.info("Worker completed successfully")
        sys.exit(0)

    except Exception as e:
        logger.error(f"Worker failed with error: {e}", exc_info=True)
        sys.exit(1)


if __name__ == "__main__":
    main()
