ALTER TABLE notification.notifications
    ADD COLUMN IF NOT EXISTS idempotency_key VARCHAR(128);

CREATE UNIQUE INDEX IF NOT EXISTS uk_notifications_created_by_idempotency_key
    ON notification.notifications(created_by, idempotency_key)
    WHERE idempotency_key IS NOT NULL;
