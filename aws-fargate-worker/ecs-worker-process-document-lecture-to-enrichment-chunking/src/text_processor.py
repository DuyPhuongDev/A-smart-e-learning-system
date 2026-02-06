"""
Text Processor Module
Handles text lecture chunking with token-based splitting and overlap.
"""

import logging
import re
from dataclasses import dataclass
from typing import List

from .config import Config

logger = logging.getLogger(__name__)


@dataclass
class TextChunk:
    """A chunk of text content."""
    chunk_index: int
    text: str
    word_count: int
    token_count: int
    start_position: int  # Character position in original text
    end_position: int


class TextProcessor:
    """
    Processes text lectures:
    1. Splits text into ~500 token chunks
    2. Maintains 50 token overlap between chunks
    3. Respects paragraph boundaries where possible
    """

    def __init__(self):
        self.chunk_size = Config.TEXT_CHUNK_SIZE  # ~500 tokens
        self.overlap_size = Config.TEXT_CHUNK_OVERLAP  # ~50 tokens
        logger.info(f"TextProcessor initialized with chunk_size: {self.chunk_size}, overlap: {self.overlap_size}")

    def split_text_into_chunks(self, text: str) -> List[TextChunk]:
        """
        Split text into chunks with overlap.

        Strategy:
        - Target ~500 tokens per chunk
        - 50 token overlap between chunks
        - Try to break at paragraph/sentence boundaries

        Args:
            text: Raw text content

        Returns:
            List of TextChunk objects
        """
        if not text or not text.strip():
            return []

        # Clean text
        text = self._clean_text(text)
        total_tokens = self._estimate_tokens(text)

        logger.info(f"Splitting text with {total_tokens} estimated tokens")

        # If text is small enough, return as single chunk
        if total_tokens <= self.chunk_size:
            return [TextChunk(
                chunk_index=0,
                text=text,
                word_count=len(text.split()),
                token_count=total_tokens,
                start_position=0,
                end_position=len(text)
            )]

        chunks = []
        # Split into paragraphs first
        paragraphs = self._split_into_paragraphs(text)

        current_text = ""
        current_tokens = 0
        current_start = 0
        chunk_index = 0

        for para in paragraphs:
            para_tokens = self._estimate_tokens(para)

            # Handle case where single paragraph exceeds chunk size
            if para_tokens > self.chunk_size and not current_text:
                # Split large paragraph into smaller pieces
                para_chunks = self._split_large_paragraph(para, self.chunk_size)
                for i, para_chunk in enumerate(para_chunks):
                    chunk_tokens = self._estimate_tokens(para_chunk)
                    chunks.append(TextChunk(
                        chunk_index=chunk_index,
                        text=para_chunk.strip(),
                        word_count=len(para_chunk.split()),
                        token_count=chunk_tokens,
                        start_position=current_start,
                        end_position=current_start + len(para_chunk)
                    ))
                    chunk_index += 1
                    current_start += len(para_chunk)
                continue

            # If adding this paragraph exceeds limit
            if current_tokens + para_tokens > self.chunk_size and current_text:
                # Save current chunk
                chunks.append(TextChunk(
                    chunk_index=chunk_index,
                    text=current_text.strip(),
                    word_count=len(current_text.split()),
                    token_count=current_tokens,
                    start_position=current_start,
                    end_position=current_start + len(current_text)
                ))
                chunk_index += 1

                # Start new chunk with overlap
                overlap_text = self._get_overlap_text(current_text, self.overlap_size)
                current_text = overlap_text + "\n\n" + para if overlap_text else para
                current_tokens = self._estimate_tokens(current_text)
                current_start = current_start + len(current_text) - len(overlap_text) - len(para)
            else:
                if current_text:
                    current_text += "\n\n" + para
                else:
                    current_text = para
                current_tokens += para_tokens

        # Don't forget last chunk
        if current_text.strip():
            chunks.append(TextChunk(
                chunk_index=chunk_index,
                text=current_text.strip(),
                word_count=len(current_text.split()),
                token_count=current_tokens,
                start_position=current_start,
                end_position=current_start + len(current_text)
            ))

        logger.info(f"Created {len(chunks)} text chunks")
        return chunks

    def _split_into_paragraphs(self, text: str) -> List[str]:
        """Split text into paragraphs."""
        # Split on double newlines or multiple newlines
        paragraphs = re.split(r'\n\s*\n', text)
        return [p.strip() for p in paragraphs if p.strip()]

    def _get_overlap_text(self, text: str, target_tokens: int) -> str:
        """Get the last N tokens worth of text for overlap."""
        if not text:
            return ""

        words = text.split()
        # Approximate: 1 word ≈ 1.3 tokens
        target_words = int(target_tokens / 1.3)

        if len(words) <= target_words:
            return text

        overlap_words = words[-target_words:]
        return " ".join(overlap_words)

    def _clean_text(self, text: str) -> str:
        """Clean text content."""
        if not text:
            return ""

        # Remove excessive whitespace while preserving paragraph breaks
        text = re.sub(r'\n{3,}', '\n\n', text)
        text = re.sub(r'[ \t]+', ' ', text)
        text = re.sub(r' +\n', '\n', text)

        return text.strip()

    def _estimate_tokens(self, text: str) -> int:
        """Estimate token count (approximately 4 characters per token)."""
        if not text:
            return 0
        return int(len(text) / 4)

    def _split_large_paragraph(self, text: str, max_tokens: int) -> List[str]:
        """Split a large paragraph into smaller chunks by sentences."""
        import re
        
        # Split by sentences
        sentences = re.split(r'(?<=[.!?])\s+', text)
        
        chunks = []
        current_chunk = ""
        current_tokens = 0
        
        for sentence in sentences:
            sentence_tokens = self._estimate_tokens(sentence)
            
            if current_tokens + sentence_tokens > max_tokens and current_chunk:
                chunks.append(current_chunk.strip())
                current_chunk = sentence
                current_tokens = sentence_tokens
            else:
                current_chunk = current_chunk + " " + sentence if current_chunk else sentence
                current_tokens += sentence_tokens
        
        if current_chunk.strip():
            chunks.append(current_chunk.strip())
        
        return chunks if chunks else [text]
