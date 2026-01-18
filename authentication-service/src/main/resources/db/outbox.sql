-- Outbox table for Saga Choreography Pattern with Debezium CDC
-- Authentication Service outbox for publishing compensating transaction events

CREATE TABLE IF NOT EXISTS authentication.outbox (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id UUID NOT NULL,
    type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_auth_outbox_aggregate_id ON authentication.outbox(aggregate_id);
CREATE INDEX IF NOT EXISTS idx_auth_outbox_created_at ON authentication.outbox(created_at);
