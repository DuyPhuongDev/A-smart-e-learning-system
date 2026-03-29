"""
Main Entry Point for ECS Document/Text Enrichment Worker
Polls SQS queue for document/text enrichment jobs, processes them, and sends results back.

Features:
- Long polling SQS for efficiency
- Dual processing: Document (PDF/DOCX/PPTX) and Text lectures
- Gotenberg integration for document conversion
- Gemini LLM for content enrichment
- Automatic exit after MAX_EMPTY_POLLS consecutive empty polls (cost optimization)
- Error handling with SQS retry mechanism
"""

import json
import logging
import sys
import os
import signal
import time
from typing import Optional, Dict, Any, List
from concurrent.futures import ThreadPoolExecutor, as_completed

import boto3
from botocore.exceptions import ClientError

from .config import Config
from .document_downloader import DocumentDownloader
from .gotenberg_converter import GotenbergConverter
from .pdf_processor import PDFProcessor, PageWindow
from .text_processor import TextProcessor, TextChunk
from .llm_enricher import LLMEnricher, EnrichedChunk
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
    """Handle shutdown signals gracefully."""
    global shutdown_requested
    logger.info(f"Received signal {signum}, initiating graceful shutdown...")
    shutdown_requested = True


class SQSMessage:
    """Parsed SQS message containing job details."""

    def __init__(self, raw_message: Dict):
        self.receipt_handle = raw_message['ReceiptHandle']
        self.message_id = raw_message['MessageId']

        # Parse message body
        body = json.loads(raw_message['Body'])

        self.lecture_id: str = body['lectureId']
        self.lecture_type: str = body['lectureType']  # 'DOCUMENT' or 'TEXT'
        self.callback_url: str = body['callbackUrl']
        self.lecture_title: str = body.get('lectureTitle', 'Unknown')

        # Document-specific fields
        self.download_url: Optional[str] = body.get('downloadUrl')
        self.source_type: Optional[str] = body.get('sourceType')
        self.file_format: Optional[str] = body.get('fileFormat')
        self.num_pages: Optional[int] = body.get('numPages')

        # Text-specific fields
        self.text_content: Optional[str] = body.get('textContent')
        self.format_type: Optional[str] = body.get('formatType')
        self.word_count: Optional[int] = body.get('wordCount')

    def __str__(self):
        return (f"SQSMessage(id={self.message_id}, lecture={self.lecture_id}, "
                f"type={self.lecture_type})")


class Worker:
    """
    Main worker class that orchestrates the document/text enrichment pipeline:
    1. Poll SQS for messages
    2. Download document OR process text content
    3. Convert to PDF if needed (via Gotenberg)
    4. Extract text and create page windows
    5. Enrich with Gemini LLM
    6. Send results to backend via callback
    7. Delete message from SQS on success
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
        self.document_downloader = DocumentDownloader()
        self.gotenberg_converter = GotenbergConverter()
        self.pdf_processor = PDFProcessor()
        self.text_processor = TextProcessor()
        self.llm_enricher = LLMEnricher()
        self.callback_handler = CallbackHandler()

        # Worker state
        self.empty_poll_count = 0
        self.processed_count = 0
        self.failed_count = 0

        logger.info("Worker initialized successfully")

    def run(self):
        """Main worker loop."""
        logger.info("Starting worker loop...")

        while not shutdown_requested:
            try:
                # Poll for messages
                message = self._poll_message()

                if message is None:
                    self.empty_poll_count += 1
                    logger.info(f"No messages received. Empty poll count: {self.empty_poll_count}/{Config.MAX_EMPTY_POLLS}")

                    if self.empty_poll_count >= Config.MAX_EMPTY_POLLS:
                        logger.info(f"Reached MAX_EMPTY_POLLS ({Config.MAX_EMPTY_POLLS}). Exiting worker.")
                        break

                    continue

                # Reset empty poll counter on successful message receive
                self.empty_poll_count = 0

                # Process the message
                success = self._process_message(message)

                if success:
                    self._delete_message(message)
                    self.processed_count += 1
                    logger.info(f"Successfully processed lecture {message.lecture_id}. "
                               f"Total processed: {self.processed_count}")
                else:
                    self.failed_count += 1
                    logger.error(f"Failed to process lecture {message.lecture_id}. "
                                f"Message will be retried. Total failures: {self.failed_count}")

            except KeyboardInterrupt:
                logger.info("Keyboard interrupt received. Shutting down...")
                break
            except Exception as e:
                logger.error(f"Unexpected error in worker loop: {e}", exc_info=True)
                time.sleep(5)

        self._cleanup()
        logger.info(f"Worker stopped. Processed: {self.processed_count}, Failed: {self.failed_count}")

    def _poll_message(self) -> Optional[SQSMessage]:
        """Poll SQS for a single message using long polling."""
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
        """Process a single enrichment job."""
        logger.info(f"Processing lecture: {message.lecture_id} ({message.lecture_type})")

        try:
            if message.lecture_type == "DOCUMENT":
                return self._process_document(message)
            elif message.lecture_type == "TEXT":
                return self._process_text(message)
            else:
                logger.error(f"Unknown lecture type: {message.lecture_type}")
                return False

        except Exception as e:
            logger.error(f"Error processing message: {e}", exc_info=True)
            return False

    def _process_document(self, message: SQSMessage) -> bool:
        """
        Process document lecture.

        Pipeline:
        1. Download document from S3/CloudFront
        2. Convert to PDF if needed (via Gotenberg)
        3. Extract text from each page
        4. Create page windows with overlapping context
        5. Enrich each window with LLM
        6. Send results to backend
        """
        logger.info(f"Processing DOCUMENT lecture: {message.lecture_id}")

        # Validate required fields for document processing
        if not message.download_url:
            logger.error(f"Missing download_url for document lecture {message.lecture_id}")
            return False

        downloaded_file = None
        converted_file = None

        try:
            # Step 1: Download document
            logger.info("Step 1: Downloading document...")
            downloaded_file = self.document_downloader.download_document(
                download_url=message.download_url,
                lecture_id=message.lecture_id,
                file_format=message.file_format
            )

            if not downloaded_file:
                logger.error(f"Failed to download document for lecture {message.lecture_id}")
                return False

            logger.info(f"Document downloaded: {downloaded_file}")

            # Step 2: Convert to PDF if needed
            logger.info("Step 2: Converting to PDF if needed...")
            if self.gotenberg_converter.needs_conversion(downloaded_file):
                converted_file = self.gotenberg_converter.convert_to_pdf(
                    downloaded_file, message.lecture_id
                )
                if not converted_file:
                    logger.error(f"Failed to convert document to PDF")
                    return False
                pdf_path = converted_file
            else:
                pdf_path = downloaded_file

            logger.info(f"PDF ready: {pdf_path}")

            # Step 3: Extract text from pages
            logger.info("Step 3: Extracting text from PDF pages...")
            pages = self.pdf_processor.extract_pages(pdf_path)

            if not pages:
                logger.error(f"No pages extracted from PDF")
                return False

            # Filter out pages with no meaningful text
            pages_with_text = [p for p in pages if p.text and p.text.strip()]
            if not pages_with_text:
                logger.warning(f"All {len(pages)} pages are empty or contain no text")
                # Still process but log warning - might be image-only PDF

            logger.info(f"Extracted {len(pages)} pages")

            # Step 4: Create page windows
            logger.info("Step 4: Creating page windows...")
            windows = self.pdf_processor.create_page_windows(pages)
            logger.info(f"Created {len(windows)} windows")

            # Step 5: Enrich each window with LLM
            logger.info("Step 5: Enriching pages with LLM...")
            enriched_chunks = self._enrich_document_windows(windows, message.lecture_title)
            
            if not enriched_chunks:
                logger.error(f"No enriched chunks created for lecture {message.lecture_id}")
                return False
                
            logger.info(f"Created {len(enriched_chunks)} enriched chunks")

            # Step 6: Send results to backend
            logger.info("Step 6: Sending results to backend...")
            success = self.callback_handler.send_enrichment_result(
                callback_url=message.callback_url,
                lecture_id=message.lecture_id,
                content_type="DOCUMENT",
                lecture_title=message.lecture_title,
                chunks=enriched_chunks
            )

            return success

        finally:
            # Cleanup downloaded files
            if downloaded_file:
                self.document_downloader.cleanup(downloaded_file)
            if converted_file and converted_file != downloaded_file:
                self.document_downloader.cleanup(converted_file)

    def _process_text(self, message: SQSMessage) -> bool:
        """
        Process text lecture.

        Pipeline:
        1. Split text into chunks with overlap
        2. Enrich each chunk with LLM
        3. Send results to backend
        """
        logger.info(f"Processing TEXT lecture: {message.lecture_id}")

        # Validate required fields for text processing
        if not message.text_content or not message.text_content.strip():
            logger.error(f"Missing or empty text_content for text lecture {message.lecture_id}")
            return False

        try:
            # Step 1: Split text into chunks
            logger.info("Step 1: Splitting text into chunks...")
            text_chunks = self.text_processor.split_text_into_chunks(message.text_content)

            if not text_chunks:
                logger.error(f"No chunks created from text")
                return False

            logger.info(f"Created {len(text_chunks)} text chunks")

            # Step 2: Enrich each chunk with LLM
            logger.info("Step 2: Enriching chunks with LLM...")
            enriched_chunks = self._enrich_text_chunks(text_chunks, message.lecture_title)
            
            if not enriched_chunks:
                logger.error(f"No enriched chunks created for lecture {message.lecture_id}")
                return False
                
            logger.info(f"Created {len(enriched_chunks)} enriched chunks")

            # Step 3: Send results to backend
            logger.info("Step 3: Sending results to backend...")
            success = self.callback_handler.send_enrichment_result(
                callback_url=message.callback_url,
                lecture_id=message.lecture_id,
                content_type="TEXT",
                lecture_title=message.lecture_title,
                chunks=enriched_chunks
            )

            return success

        except Exception as e:
            logger.error(f"Error processing text: {e}", exc_info=True)
            return False

    def _enrich_document_windows(
        self,
        windows: List[PageWindow],
        lecture_title: str
    ) -> List[EnrichedChunk]:
        """Enrich document page windows with LLM using batch processing."""
        # Collect all enrichment tasks
        tasks = []
        chunk_index = 0

        for window in windows:
            # Build context text (all pages except target)
            context_parts = []
            for page in window.context_pages:
                if page.page_number not in window.target_pages:
                    context_parts.append(f"[Page {page.page_number}]:\n{page.text}")
            context_text = "\n\n".join(context_parts)

            # Enrich each target page
            for target_page_num in window.target_pages:
                # Find target page content
                target_page = next(
                    (p for p in window.context_pages if p.page_number == target_page_num),
                    None
                )

                if not target_page or not target_page.text.strip():
                    continue

                tasks.append({
                    'chunk_index': chunk_index,
                    'target_text': target_page.text,
                    'context_text': context_text,
                    'page_number': target_page_num
                })
                chunk_index += 1

        # Process in batches with concurrent execution
        return self._process_enrichment_batches(tasks)

    def _enrich_text_chunks(
        self,
        chunks: List[TextChunk],
        lecture_title: str
    ) -> List[EnrichedChunk]:
        """Enrich text chunks with LLM using sliding context and batch processing."""
        tasks = []
        context_window_size = 5

        for i, chunk in enumerate(chunks):
            # Build context from surrounding chunks
            context_parts = []
            half_window = context_window_size // 2

            start = max(0, i - half_window)
            end = min(len(chunks) - 1, i + half_window)

            for j in range(start, end + 1):
                if j != i:
                    context_parts.append(f"[Chunk {j + 1}]:\n{chunks[j].text}")

            context_text = "\n\n".join(context_parts)

            tasks.append({
                'chunk_index': chunk.chunk_index,
                'target_text': chunk.text,
                'context_text': context_text,
                'page_number': None  # Text lectures don't have page numbers
            })

        # Process in batches with concurrent execution
        return self._process_enrichment_batches(tasks)

    def _process_enrichment_batches(self, tasks: List[Dict]) -> List[EnrichedChunk]:
        """Process enrichment tasks in batches using ThreadPoolExecutor."""
        enriched_chunks = []
        batch_size = Config.LLM_BATCH_SIZE
        total_tasks = len(tasks)

        logger.info(f"Processing {total_tasks} tasks in batches of {batch_size}")

        # Process in batches
        for batch_start in range(0, total_tasks, batch_size):
            batch_end = min(batch_start + batch_size, total_tasks)
            batch = tasks[batch_start:batch_end]
            
            logger.info(f"Processing batch {batch_start//batch_size + 1}/{(total_tasks + batch_size - 1)//batch_size}: "
                       f"tasks {batch_start}-{batch_end-1}")

            # Execute batch concurrently
            with ThreadPoolExecutor(max_workers=batch_size) as executor:
                futures = {}
                
                for task in batch:
                    future = executor.submit(
                        self.llm_enricher.enrich_chunk,
                        chunk_index=task['chunk_index'],
                        target_text=task['target_text'],
                        context_text=task['context_text'],
                        page_number=task.get('page_number')
                    )
                    futures[future] = task['chunk_index']

                # Collect results in order
                batch_results = []
                for future in as_completed(futures):
                    try:
                        result = future.result()
                        batch_results.append((futures[future], result))
                    except Exception as e:
                        chunk_idx = futures[future]
                        logger.error(f"Error enriching chunk {chunk_idx}: {e}")
                        # Create fallback chunk on error
                        task = next(t for t in batch if t['chunk_index'] == chunk_idx)
                        fallback = self.llm_enricher._create_fallback_chunk(
                            chunk_idx,
                            task['target_text'],
                            task.get('page_number'),
                            None,
                            None
                        )
                        batch_results.append((chunk_idx, fallback))

                # Sort by chunk index to maintain order
                batch_results.sort(key=lambda x: x[0])
                enriched_chunks.extend([result for _, result in batch_results])

            # Small delay between batches to respect rate limits
            if batch_end < total_tasks:
                time.sleep(Config.LLM_DELAY_MS / 1000)
                logger.debug(f"Batch delay: {Config.LLM_DELAY_MS}ms")

        logger.info(f"Completed processing {len(enriched_chunks)} chunks")
        return enriched_chunks

    def _delete_message(self, message: SQSMessage):
        """Delete message from SQS after successful processing."""
        try:
            self.sqs_client.delete_message(
                QueueUrl=self.queue_url,
                ReceiptHandle=message.receipt_handle
            )
            logger.info(f"Deleted message {message.message_id} from queue")
        except ClientError as e:
            logger.error(f"Failed to delete message: {e}")

    def _cleanup(self):
        """Cleanup resources before shutdown."""
        logger.info("Cleaning up resources...")

        try:
            self.document_downloader.cleanup_all()
            self.callback_handler.close()
        except Exception as e:
            logger.warning(f"Cleanup error: {e}")


def main():
    """Main entry point."""
    # Register signal handlers for graceful shutdown
    signal.signal(signal.SIGTERM, signal_handler)
    signal.signal(signal.SIGINT, signal_handler)

    logger.info("=" * 60)
    logger.info("ECS Worker for Document/Text Enrichment - Starting")
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
