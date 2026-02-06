-- V4: Add message_knowledge_sources table to store knowledge sources for assistant messages
-- This allows users to trace back to the original lecture content

CREATE TABLE coaching_chatbot.message_knowledge_sources (
    id UUID NOT NULL,
    message_id UUID NOT NULL,
    chunk_id UUID NOT NULL,
    chunk_index INTEGER NULL,
    page_number INTEGER NULL,
    start_time_seconds INTEGER NULL,
    end_time_seconds INTEGER NULL,
    CONSTRAINT message_knowledge_sources_pkey PRIMARY KEY (id),
    CONSTRAINT message_knowledge_sources_message_fkey FOREIGN KEY (message_id)
        REFERENCES coaching_chatbot.chat_messages(message_id) ON DELETE CASCADE
);

-- Indexes for efficient queries
CREATE INDEX idx_message_knowledge_sources_message_id ON coaching_chatbot.message_knowledge_sources(message_id);
CREATE INDEX idx_message_knowledge_sources_chunk_id ON coaching_chatbot.message_knowledge_sources(chunk_id);

-- Comments for documentation
COMMENT ON TABLE coaching_chatbot.message_knowledge_sources IS 'Stores knowledge source references for assistant messages to help users trace back to original lecture content';
COMMENT ON COLUMN coaching_chatbot.message_knowledge_sources.chunk_id IS 'Reference to the knowledge chunk used for generating the response';
COMMENT ON COLUMN coaching_chatbot.message_knowledge_sources.chunk_index IS 'Position of the chunk in the lecture content';
COMMENT ON COLUMN coaching_chatbot.message_knowledge_sources.page_number IS 'Page number for document-based sources';
COMMENT ON COLUMN coaching_chatbot.message_knowledge_sources.start_time_seconds IS 'Start time in seconds for video-based sources';
COMMENT ON COLUMN coaching_chatbot.message_knowledge_sources.end_time_seconds IS 'End time in seconds for video-based sources';
