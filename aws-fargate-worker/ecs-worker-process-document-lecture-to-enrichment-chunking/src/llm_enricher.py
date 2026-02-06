"""
LLM Enricher Module
Handles enrichment of document/text content using Gemini LLM.
"""

import json
import logging
import time
from dataclasses import dataclass
from typing import List, Optional

from google import genai
from google.genai import types

from .config import Config

logger = logging.getLogger(__name__)


@dataclass
class EnrichedChunk:
    """Result of LLM enrichment for a chunk."""
    chunk_index: int
    original_text: str
    summary: str
    questions: List[str]
    enriched_content: str
    page_number: Optional[int]  # For documents
    start_page: Optional[int]
    end_page: Optional[int]
    token_count: int
    is_enriched: bool


class LLMEnricher:
    """
    Enriches document/text content using Gemini LLM.
    Generates summaries and questions for each chunk.

    Same prompt style as TranscriptEnrichmentServiceImpl in Java backend.
    """

    ENRICHMENT_PROMPT = """You are an educational content expert. Your task is to enrich a document/text content segment.

CRITICAL LANGUAGE REQUIREMENT:
- Detect the language of the TARGET SEGMENT below
- Your "summary" and "questions" MUST be written in the SAME LANGUAGE as the TARGET SEGMENT
- If the content is in Vietnamese, write summary and questions in Vietnamese
- If the content is in English, write summary and questions in English

CONTEXT (surrounding content for reference):
{context}

TARGET SEGMENT TO ENRICH (this is the main segment you need to process):
{target_segment}

Please provide the following in JSON format:
1. "original_text": The exact original text (copy as-is from TARGET SEGMENT)
2. "summary": A concise summary of what this segment teaches (maximum 100 words) - MUST BE IN THE SAME LANGUAGE AS THE CONTENT
3. "questions": An array of exactly 4 educational questions about this content - MUST BE IN THE SAME LANGUAGE AS THE CONTENT

IMPORTANT:
- Output ONLY valid JSON, no markdown code blocks or explanations
- The response must be parseable JSON
- Questions should be meaningful and test understanding of the content
- Summary should capture the key learning points
- ALWAYS match the output language to the input content language

JSON Response format:
{{"original_text": "...", "summary": "...", "questions": ["Q1?", "Q2?", "Q3?", "Q4?"]}}
"""

    def __init__(self):
        self.api_key = Config.GEMINI_API_KEY
        self.model_name = Config.GEMINI_MODEL
        self.delay_ms = Config.LLM_DELAY_MS
        self.max_retries = Config.LLM_MAX_RETRIES

        # Initialize Gemini client (new SDK for Gemini 3)
        self.client = genai.Client(api_key=self.api_key)

        logger.info(f"LLMEnricher initialized with model: {self.model_name}")

    def enrich_chunk(
        self,
        chunk_index: int,
        target_text: str,
        context_text: str,
        page_number: Optional[int] = None,
        start_page: Optional[int] = None,
        end_page: Optional[int] = None
    ) -> EnrichedChunk:
        """
        Enrich a single chunk with LLM.

        Args:
            chunk_index: Index of the chunk
            target_text: The main text to enrich
            context_text: Surrounding context for better understanding
            page_number: Page number (for documents)
            start_page: Start page (for multi-page chunks)
            end_page: End page (for multi-page chunks)

        Returns:
            EnrichedChunk with enrichment results
        """
        logger.debug(f"Enriching chunk {chunk_index}...")

        for attempt in range(1, self.max_retries + 1):
            try:
                prompt = self.ENRICHMENT_PROMPT.format(
                    context=context_text if context_text else "No additional context available.",
                    target_segment=target_text
                )

                # Use new SDK for Gemini 3
                response = self.client.models.generate_content(
                    model=self.model_name,
                    contents=prompt
                )

                if not response or not response.text:
                    raise ValueError("Empty response from Gemini")

                # Parse response
                enriched = self._parse_response(
                    response.text,
                    chunk_index,
                    target_text,
                    page_number,
                    start_page,
                    end_page
                )

                if enriched and enriched.is_enriched:
                    logger.debug(f"Chunk {chunk_index} enriched successfully on attempt {attempt}")
                    return enriched

            except Exception as e:
                logger.error(f"Full error for chunk {chunk_index} attempt {attempt}: {type(e).__name__}: {str(e)}")
                error_str = str(e).lower()
                # Handle rate limiting
                if 'rate' in error_str or 'quota' in error_str or '429' in error_str or 'resource' in error_str:
                    wait_time = min(self.delay_ms * attempt / 1000, 30)  # Max 30 seconds
                    logger.warning(f"Rate limited on attempt {attempt}, waiting {wait_time}s...")
                    time.sleep(wait_time)
                else:
                    logger.warning(f"Enrichment attempt {attempt} failed for chunk {chunk_index}: {e}")

                if attempt < self.max_retries:
                    time.sleep(self.delay_ms / 1000)

        # All retries failed - return fallback
        logger.warning(f"All {self.max_retries} retries failed for chunk {chunk_index}, using fallback")
        return self._create_fallback_chunk(
            chunk_index, target_text, page_number, start_page, end_page
        )

    def _parse_response(
        self,
        response_text: str,
        chunk_index: int,
        original_text: str,
        page_number: Optional[int],
        start_page: Optional[int],
        end_page: Optional[int]
    ) -> Optional[EnrichedChunk]:
        """Parse LLM response JSON into EnrichedChunk."""
        try:
            # Clean response - remove markdown code blocks if present
            cleaned = response_text.strip()
            if cleaned.startswith("```json"):
                cleaned = cleaned[7:]
            if cleaned.startswith("```"):
                cleaned = cleaned[3:]
            if cleaned.endswith("```"):
                cleaned = cleaned[:-3]
            cleaned = cleaned.strip()

            data = json.loads(cleaned)

            text = data.get("original_text", original_text)
            summary = data.get("summary", "")
            questions = data.get("questions", [])

            if not isinstance(questions, list):
                questions = []

            # Build enriched content
            enriched_content = self._build_enriched_content(text, summary, questions)
            token_count = self._estimate_tokens(enriched_content)

            return EnrichedChunk(
                chunk_index=chunk_index,
                original_text=text,
                summary=summary,
                questions=questions,
                enriched_content=enriched_content,
                page_number=page_number,
                start_page=start_page,
                end_page=end_page,
                token_count=token_count,
                is_enriched=True
            )

        except json.JSONDecodeError as e:
            logger.warning(f"Failed to parse LLM response as JSON: {e}")
            return None

    def _build_enriched_content(
        self,
        original_text: str,
        summary: str,
        questions: List[str]
    ) -> str:
        """Build the final enriched content string."""
        content = []

        content.append("=== ORIGINAL CONTENT ===")
        content.append(original_text)
        content.append("")

        content.append("=== SUMMARY ===")
        content.append(summary)
        content.append("")

        content.append("=== REVIEW QUESTIONS ===")
        for i, q in enumerate(questions, 1):
            content.append(f"{i}. {q}")

        return "\n".join(content)

    def _create_fallback_chunk(
        self,
        chunk_index: int,
        original_text: str,
        page_number: Optional[int],
        start_page: Optional[int],
        end_page: Optional[int]
    ) -> EnrichedChunk:
        """Create fallback chunk with original text only."""
        enriched_content = self._build_enriched_content(
            original_text,
            "Summary not available.",
            ["Question not available."]
        )

        return EnrichedChunk(
            chunk_index=chunk_index,
            original_text=original_text,
            summary="Summary not available.",
            questions=["Question not available."],
            enriched_content=enriched_content,
            page_number=page_number,
            start_page=start_page,
            end_page=end_page,
            token_count=self._estimate_tokens(enriched_content),
            is_enriched=False
        )

    def _estimate_tokens(self, text: str) -> int:
        """Estimate token count (approximately 4 characters per token)."""
        if not text:
            return 0
        return int(len(text) / 4)

    def delay_between_calls(self):
        """Wait between LLM calls to avoid rate limiting."""
        time.sleep(self.delay_ms / 1000)
