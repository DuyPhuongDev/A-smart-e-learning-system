"""
PDF Processor Module
Extracts text from PDF pages and implements overlapping window strategy.
"""

import logging
from dataclasses import dataclass
from typing import List, Optional

import fitz  # PyMuPDF

from .config import Config

logger = logging.getLogger(__name__)


@dataclass
class PageContent:
    """Content extracted from a single PDF page."""
    page_number: int
    text: str
    word_count: int
    token_count: int


@dataclass
class PageWindow:
    """
    A window of pages for context.
    Contains context pages + target page(s) for extraction.
    """
    window_index: int
    context_pages: List[PageContent]  # All pages in window (for context)
    target_pages: List[int]  # Page numbers to extract
    combined_context: str  # Combined text of context pages


class PDFProcessor:
    """
    Processes PDF documents:
    1. Extracts text from each page
    2. Implements overlapping window strategy for LLM enrichment

    Window Strategy (5-page window):
    - First window [1-5]: Extract pages 1-4 (first 4 pages)
    - Middle windows: Extract middle page only
    - Last window [n-4 to n]: Extract last 4 pages
    """

    def __init__(self):
        self.window_size = Config.PAGE_WINDOW_SIZE
        logger.info(f"PDFProcessor initialized with window_size: {self.window_size}")

    def extract_pages(self, pdf_path: str) -> List[PageContent]:
        """
        Extract text from all pages of a PDF.

        Args:
            pdf_path: Path to PDF file

        Returns:
            List of PageContent objects
        """
        logger.info(f"Extracting text from PDF: {pdf_path}")
        pages = []

        try:
            with fitz.open(pdf_path) as doc:
                total_pages = len(doc)

                for page_num in range(total_pages):
                    page = doc[page_num]
                    text = page.get_text("text")

                    # Clean up text
                    text = self._clean_text(text)
                    word_count = len(text.split()) if text else 0
                    token_count = self._estimate_tokens(text)

                    pages.append(PageContent(
                        page_number=page_num + 1,  # 1-based
                        text=text,
                        word_count=word_count,
                        token_count=token_count
                    ))

            logger.info(f"Extracted {len(pages)} pages from PDF")
            return pages

        except Exception as e:
            logger.error(f"Failed to extract PDF pages: {e}", exc_info=True)
            return []

    def create_page_windows(self, pages: List[PageContent]) -> List[PageWindow]:
        """
        Create overlapping windows for LLM processing.

        Strategy:
        - Window size: 5 pages
        - First window [1-5] → extract pages 1-4
        - Middle windows → extract middle page only
        - Last window → extract remaining pages

        Args:
            pages: List of PageContent objects

        Returns:
            List of PageWindow objects
        """
        if not pages:
            return []

        total_pages = len(pages)
        windows = []

        if total_pages <= self.window_size:
            # All pages fit in one window - extract all
            windows.append(PageWindow(
                window_index=0,
                context_pages=pages,
                target_pages=[p.page_number for p in pages],
                combined_context=self._combine_pages_text(pages)
            ))
            return windows

        # Track which pages have been assigned for extraction
        extracted_pages = set()
        window_index = 0

        # First window: pages 1-5, extract pages 1-4
        first_window_pages = pages[:self.window_size]
        first_extract = [p.page_number for p in first_window_pages[:self.window_size - 1]]
        windows.append(PageWindow(
            window_index=window_index,
            context_pages=first_window_pages,
            target_pages=first_extract,
            combined_context=self._combine_pages_text(first_window_pages)
        ))
        extracted_pages.update(first_extract)
        window_index += 1

        # Middle windows: slide through remaining pages
        # Each window extracts only the middle page
        start = 2  # Start from page 3 (0-indexed: 2) as center
        while start + self.window_size - 1 <= total_pages:
            # Check if we're at the last possible window
            if start + self.window_size >= total_pages:
                break

            window_start = start - (self.window_size // 2)
            window_end = window_start + self.window_size
            window_pages = pages[window_start:window_end]

            # Middle page index within window
            middle_idx = self.window_size // 2
            target_page = window_pages[middle_idx].page_number

            if target_page not in extracted_pages:
                windows.append(PageWindow(
                    window_index=window_index,
                    context_pages=window_pages,
                    target_pages=[target_page],
                    combined_context=self._combine_pages_text(window_pages)
                ))
                extracted_pages.add(target_page)
                window_index += 1

            start += 1

        # Last window: extract remaining unextracted pages
        remaining_pages = [p for p in pages if p.page_number not in extracted_pages]
        if remaining_pages:
            # Use last N pages as context
            last_window_start = max(0, total_pages - self.window_size)
            last_window_pages = pages[last_window_start:]
            remaining_page_numbers = [p.page_number for p in remaining_pages]

            windows.append(PageWindow(
                window_index=window_index,
                context_pages=last_window_pages,
                target_pages=remaining_page_numbers,
                combined_context=self._combine_pages_text(last_window_pages)
            ))

        logger.info(f"Created {len(windows)} windows for {total_pages} pages")
        return windows

    def _combine_pages_text(self, pages: List[PageContent]) -> str:
        """Combine text from multiple pages with page markers."""
        parts = []
        for page in pages:
            if page.text.strip():
                parts.append(f"[Page {page.page_number}]:\n{page.text}")
        return "\n\n".join(parts)

    def _clean_text(self, text: str) -> str:
        """Clean extracted text."""
        if not text:
            return ""
        # Remove excessive whitespace
        lines = text.split('\n')
        cleaned_lines = [line.strip() for line in lines if line.strip()]
        return '\n'.join(cleaned_lines)

    def _estimate_tokens(self, text: str) -> int:
        """Estimate token count (approximately 4 characters per token)."""
        if not text:
            return 0
        return int(len(text) / 4)
