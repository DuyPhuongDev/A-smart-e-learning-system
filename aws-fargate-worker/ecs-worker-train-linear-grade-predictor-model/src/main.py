"""
Main Entry Point for Model Training ECS Worker
Polls SQS queue for training jobs, processes them, and sends results back.

Features:
- Long polling SQS for efficiency
- Auto-exit after MAX_EMPTY_POLLS consecutive empty polls (cost optimization)
- Error handling with SQS retry mechanism
- Cleanup of temporary files
"""

import json
import logging
import sys
import signal
import time
from typing import Optional, Dict

import boto3
from botocore.exceptions import ClientError

from .config import Config
from .data_loader import DataLoader
from .model_trainer import ModelTrainer
from .callback import CallbackHandler

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[logging.StreamHandler(sys.stdout)]
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
    """Parsed SQS message containing training job details"""

    def __init__(self, raw_message: Dict):
        self.receipt_handle = raw_message['ReceiptHandle']
        self.message_id = raw_message['MessageId']

        # Parse message body
        body = json.loads(raw_message['Body'])

        self.job_id: str = body['job_id']
        self.s3_dataset_url: str = body['s3_dataset_url']
        self.model_name: str = body['model_name']
        self.timestamp: str = body['timestamp']

    def __str__(self):
        return (f"SQSMessage(id={self.message_id}, job={self.job_id}, "
                f"model={self.model_name})")


class Worker:
    """Main worker orchestrates the model training pipeline"""

    def __init__(self):
        logger.info("Initializing Worker...")

        # Validate configuration
        Config.validate()
        Config.print_config()

        # Initialize AWS SQS client
        self.sqs_client = boto3.client('sqs', region_name=Config.AWS_REGION)
        self.queue_url = Config.SQS_QUEUE_URL

        # Initialize handlers
        self.data_loader = DataLoader()
        self.model_trainer = ModelTrainer()
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
                    logger.info(
                        f"No messages received. Empty poll count: "
                        f"{self.empty_poll_count}/{Config.MAX_EMPTY_POLLS}"
                    )

                    # Exit if too many empty polls (Fargate cost optimization)
                    if self.empty_poll_count >= Config.MAX_EMPTY_POLLS:
                        logger.info(
                            f"Reached MAX_EMPTY_POLLS ({Config.MAX_EMPTY_POLLS}). "
                            "Exiting worker."
                        )
                        break

                    continue

                # Reset empty poll counter on successful message receive
                self.empty_poll_count = 0

                # Process the message
                success = self._process_message(message)

                # ALWAYS delete message from queue (success or failure)
                # This prevents infinite retries and ensures cleanup
                self._delete_message(message)

                if success:
                    self.processed_count += 1
                    logger.info(
                        f"Successfully processed job {message.job_id}. "
                        f"Total processed: {self.processed_count}"
                    )
                else:
                    self.failed_count += 1
                    logger.error(
                        f"Failed to process job {message.job_id}. "
                        f"Total failures: {self.failed_count}"
                    )

            except KeyboardInterrupt:
                logger.info("Keyboard interrupt received. Shutting down...")
                break
            except Exception as e:
                logger.error(f"Unexpected error in worker loop: {e}", exc_info=True)
                time.sleep(5)

        self._cleanup()
        logger.info(
            f"Worker stopped. Processed: {self.processed_count}, "
            f"Failed: {self.failed_count}"
        )

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
        """Pipeline: Download → Train → Upload → Callback"""
        logger.info(f"Processing training job: {message.job_id}")

        csv_path = None
        model_local_path = None
        start_time = time.time()

        try:
            # Step 1: Download dataset from S3
            logger.info(f"Step 1: Downloading dataset from {message.s3_dataset_url}")
            csv_path = self.data_loader.download_csv_from_s3(
                s3_url=message.s3_dataset_url,
                job_id=message.job_id
            )

            # Step 2: Load and split data
            logger.info("Step 2: Loading and splitting data")
            df_train, df_test = self.data_loader.load_and_split_data(csv_path)

            # Step 3: Train model
            logger.info("Step 3: Training model")
            model, feature_cols, metrics = self.model_trainer.train(df_train, df_test)

            # Step 4: Save and upload model to S3
            logger.info("Step 4: Saving and uploading model to S3")
            model_s3_path, model_local_path = self.model_trainer.save_and_upload_model(
                model=model,
                feature_cols=feature_cols,
                metrics=metrics,
                model_name=message.model_name,
                timestamp=message.timestamp
            )

            training_time = time.time() - start_time

            # Step 5: Send results to backend
            logger.info("Step 5: Sending results to backend")
            callback_success = self.callback_handler.send_training_result(
                job_id=message.job_id,
                model_name=message.model_name,
                model_s3_path=model_s3_path,
                metrics=metrics,
                training_time_seconds=training_time,
                status="SUCCESS"
            )

            if not callback_success:
                logger.error(f"Callback failed for job {message.job_id}")
                return False

            logger.info(
                f"Successfully processed job {message.job_id} "
                f"in {training_time:.2f}s"
            )
            return True

        except Exception as e:
            logger.error(f"Error processing message: {e}", exc_info=True)

            # Try to send error callback
            try:
                self.callback_handler.send_error(
                    job_id=message.job_id,
                    model_name=message.model_name,
                    error_message=str(e)
                )
            except Exception as callback_error:
                logger.warning(f"Failed to send error callback: {callback_error}")

            return False

        finally:
            # Cleanup all temporary files
            logger.info("Cleaning up temporary files...")

            # Cleanup CSV dataset file
            if csv_path:
                self.data_loader.cleanup(csv_path)

            # Cleanup local model file if it exists
            if model_local_path:
                self.model_trainer.cleanup(model_local_path)

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
            self.callback_handler.close()
        except Exception as e:
            logger.warning(f"Cleanup error: {e}")


def main():
    """Main entry point"""
    signal.signal(signal.SIGTERM, signal_handler)
    signal.signal(signal.SIGINT, signal_handler)

    logger.info("=" * 60)
    logger.info("ECS Worker for Model Training - Starting")
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
