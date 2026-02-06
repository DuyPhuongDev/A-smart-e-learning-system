-- Migration: V5__Enhance_video_transcripts_table.sql
-- Description: Add new columns to video_transcripts table for better transcript management
-- Columns added:
--   - language_code: Language of the transcript (e.g., 'en', 'vi')
--   - audio_duration: Duration of audio in seconds
--   - word_count: Number of words in the transcript
--   - start_time_seconds: Start time of transcript segment in seconds
--   - end_time_seconds: End time of transcript segment in seconds
--   - segment_index: Index of transcript segment (0, 1, 2, ...)
-- Indexes added:
--   - idx_video_transcripts_video_lecture_id: Fast lookup by lecture
--   - idx_video_transcripts_time_range: Fast lookup by time range
--   - idx_video_transcripts_segment: Fast lookup by segment

-- Step 1: Add new columns to video_transcripts table
ALTER TABLE course_management.video_transcripts
ADD COLUMN language_code varchar NOT NULL DEFAULT 'en',
ADD COLUMN audio_duration int4 NULL,
ADD COLUMN word_count int4 NULL,
ADD COLUMN start_time_seconds int4 NOT NULL DEFAULT 0,
ADD COLUMN end_time_seconds int4 NULL,
ADD COLUMN segment_index int4 NULL;

-- Step 2: Create indexes for better query performance
-- Index for quick lookup by video lecture
CREATE INDEX idx_video_transcripts_video_lecture_id
ON course_management.video_transcripts(video_lecture_id);

-- Index for quick lookup by time range
CREATE INDEX idx_video_transcripts_time_range
ON course_management.video_transcripts(video_lecture_id, start_time_seconds, end_time_seconds);

-- Index for quick lookup by segment
CREATE INDEX idx_video_transcripts_segment
ON course_management.video_transcripts(video_lecture_id, segment_index);