-- V3: Add chat history tables for coaching chatbot
-- Chat sessions table: one session per student per lecture
CREATE TABLE coaching_chatbot.chat_sessions (
    session_id UUID NOT NULL,
    student_id UUID NOT NULL,
    lecture_id UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    CONSTRAINT chat_sessions_pkey PRIMARY KEY (session_id),
    CONSTRAINT chat_sessions_student_lecture_unique UNIQUE (student_id, lecture_id)
);

-- Indexes for efficient queries
CREATE INDEX idx_chat_sessions_student_id ON coaching_chatbot.chat_sessions(student_id);
CREATE INDEX idx_chat_sessions_lecture_id ON coaching_chatbot.chat_sessions(lecture_id);

-- Chat messages table: stores conversation history
CREATE TABLE coaching_chatbot.chat_messages (
    message_id UUID NOT NULL,
    session_id UUID NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('user', 'assistant')),
    content TEXT NOT NULL,
    language_code VARCHAR(10) NULL,
    is_error BOOLEAN DEFAULT false NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    CONSTRAINT chat_messages_pkey PRIMARY KEY (message_id),
    CONSTRAINT chat_messages_session_fkey FOREIGN KEY (session_id)
        REFERENCES coaching_chatbot.chat_sessions(session_id) ON DELETE CASCADE
);

-- Indexes for efficient queries
CREATE INDEX idx_chat_messages_session_id ON coaching_chatbot.chat_messages(session_id);
CREATE INDEX idx_chat_messages_created_at ON coaching_chatbot.chat_messages(session_id, created_at DESC);
CREATE INDEX idx_chat_messages_non_error ON coaching_chatbot.chat_messages(session_id, is_error, created_at DESC);

-- Comments for documentation
COMMENT ON TABLE coaching_chatbot.chat_sessions IS 'Stores chat sessions between students and AI coaching chatbot for each lecture';
COMMENT ON TABLE coaching_chatbot.chat_messages IS 'Stores individual messages in chat conversations, filtering out error messages from context';
COMMENT ON COLUMN coaching_chatbot.chat_messages.is_error IS 'Flag to exclude error/fallback messages from chat context window';
