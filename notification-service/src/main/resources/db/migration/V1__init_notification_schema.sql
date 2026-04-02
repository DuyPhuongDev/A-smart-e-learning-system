CREATE SCHEMA IF NOT EXISTS notification;

CREATE TABLE IF NOT EXISTS notification.notifications (
    id UUID PRIMARY KEY,
    type VARCHAR(64) NOT NULL,
    priority VARCHAR(32) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_by UUID,
    source_service VARCHAR(128),
    source_event_id VARCHAR(255),
    target_mode VARCHAR(32) NOT NULL,
    target_payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    channels JSONB NOT NULL DEFAULT '[]'::jsonb,
    scheduled_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ,
    status VARCHAR(32) NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_notifications_source_event
    ON notification.notifications(source_service, source_event_id, type);
CREATE INDEX IF NOT EXISTS idx_notifications_status_scheduled_at
    ON notification.notifications(status, scheduled_at);
CREATE INDEX IF NOT EXISTS idx_notifications_type_created_at
    ON notification.notifications(type, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_created_by
    ON notification.notifications(created_by);

CREATE TABLE IF NOT EXISTS notification.user_notifications (
    id UUID PRIMARY KEY,
    notification_id UUID NOT NULL REFERENCES notification.notifications(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,
    channel VARCHAR(16) NOT NULL,
    delivery_status VARCHAR(32) NOT NULL,
    read_status VARCHAR(16) NOT NULL,
    read_at TIMESTAMPTZ,
    delivered_at TIMESTAMPTZ,
    delivery_attempts INT NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMPTZ,
    last_error TEXT,
    frequency VARCHAR(32) NOT NULL DEFAULT 'IMMEDIATE',
    defer_reason VARCHAR(32),
    digest_bucket_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_user_notifications_notification_user_channel UNIQUE (notification_id, user_id, channel)
);

CREATE INDEX IF NOT EXISTS idx_user_notifications_user_read_created
    ON notification.user_notifications(user_id, read_status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_user_notifications_delivery_next_attempt
    ON notification.user_notifications(delivery_status, next_attempt_at, created_at);
CREATE INDEX IF NOT EXISTS idx_user_notifications_digest
    ON notification.user_notifications(user_id, digest_bucket_date, defer_reason);

CREATE TABLE IF NOT EXISTS notification.notification_preferences (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    notification_type VARCHAR(64) NOT NULL,
    in_app_enabled BOOLEAN NOT NULL DEFAULT true,
    email_enabled BOOLEAN NOT NULL DEFAULT false,
    push_enabled BOOLEAN NOT NULL DEFAULT false,
    frequency VARCHAR(32) NOT NULL DEFAULT 'IMMEDIATE',
    quiet_hours_start TIME,
    quiet_hours_end TIME,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_notification_preferences_user_type UNIQUE (user_id, notification_type)
);

CREATE INDEX IF NOT EXISTS idx_notification_preferences_user
    ON notification.notification_preferences(user_id);

CREATE TABLE IF NOT EXISTS notification.notification_templates (
    code VARCHAR(128) PRIMARY KEY,
    title_template TEXT NOT NULL,
    content_template TEXT NOT NULL,
    channel VARCHAR(16) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    version INT NOT NULL DEFAULT 1,
    updated_by UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS notification.notification_rules (
    event_type VARCHAR(128) PRIMARY KEY,
    enabled BOOLEAN NOT NULL DEFAULT true,
    notification_type VARCHAR(64) NOT NULL,
    channels JSONB NOT NULL DEFAULT '[]'::jsonb,
    priority VARCHAR(32) NOT NULL DEFAULT 'MEDIUM',
    frequency VARCHAR(32) NOT NULL DEFAULT 'IMMEDIATE',
    template_code VARCHAR(128) REFERENCES notification.notification_templates(code),
    target_mode VARCHAR(32) NOT NULL DEFAULT 'USER_LIST',
    target_payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS notification.notification_delivery_logs (
    id UUID PRIMARY KEY,
    user_notification_id UUID REFERENCES notification.user_notifications(id) ON DELETE CASCADE,
    provider VARCHAR(64) NOT NULL,
    channel VARCHAR(16) NOT NULL,
    request_payload TEXT,
    response_payload TEXT,
    response_code VARCHAR(32),
    latency_ms BIGINT,
    success BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_notification_delivery_logs_user_notification
    ON notification.notification_delivery_logs(user_notification_id);
CREATE INDEX IF NOT EXISTS idx_notification_delivery_logs_created_at
    ON notification.notification_delivery_logs(created_at DESC);

CREATE TABLE IF NOT EXISTS notification.notification_outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(128) NOT NULL,
    event_key VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    attempts INT NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_error TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_notification_outbox_event_key UNIQUE (event_key)
);

CREATE INDEX IF NOT EXISTS idx_notification_outbox_status_next_attempt
    ON notification.notification_outbox(status, next_attempt_at, created_at);

CREATE TABLE IF NOT EXISTS notification.notification_dlq (
    id UUID PRIMARY KEY,
    outbox_id UUID,
    event_type VARCHAR(128) NOT NULL,
    event_key VARCHAR(255),
    payload JSONB,
    error_message TEXT,
    attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_notification_dlq_created_at
    ON notification.notification_dlq(created_at DESC);

INSERT INTO notification.notification_templates(code, title_template, content_template, channel)
VALUES
    ('ASSIGNMENT_CREATED_IN_APP', 'Bài tập mới', 'Một bài tập mới đã được tạo cho lớp của bạn.', 'IN_APP'),
    ('DEADLINE_REMINDER_EMAIL', 'Nhắc hạn nộp', 'Bạn có bài tập sắp đến hạn nộp.', 'EMAIL'),
    ('SUBMISSION_GRADED_IN_APP', 'Bài nộp đã chấm', 'Bài nộp của bạn đã được chấm điểm.', 'IN_APP'),
    ('DISCUSSION_REPLY_IN_APP', 'Có phản hồi thảo luận mới', 'Một phản hồi mới vừa được gửi trong thảo luận.', 'IN_APP'),
    ('SYSTEM_MAINTENANCE_EMAIL', 'Thông báo bảo trì hệ thống', 'Hệ thống LMS sẽ bảo trì theo lịch đã thông báo.', 'EMAIL')
ON CONFLICT (code) DO NOTHING;

INSERT INTO notification.notification_rules(event_type, enabled, notification_type, channels, priority, frequency, template_code, target_mode, target_payload)
VALUES
    ('assessment.assignment.created', true, 'ASSIGNMENT_CREATED', '["IN_APP","EMAIL"]'::jsonb, 'MEDIUM', 'IMMEDIATE', 'ASSIGNMENT_CREATED_IN_APP', 'COURSE', '{}'::jsonb),
    ('assessment.assignment.deadline.reminder', true, 'DEADLINE_REMINDER', '["IN_APP","EMAIL"]'::jsonb, 'HIGH', 'IMMEDIATE', 'DEADLINE_REMINDER_EMAIL', 'COURSE', '{}'::jsonb),
    ('assessment.submission.graded', true, 'SUBMISSION_GRADED', '["IN_APP","EMAIL"]'::jsonb, 'HIGH', 'IMMEDIATE', 'SUBMISSION_GRADED_IN_APP', 'USER_LIST', '{}'::jsonb),
    ('communication.discussion.reply.created', true, 'DISCUSSION_REPLY', '["IN_APP"]'::jsonb, 'MEDIUM', 'IMMEDIATE', 'DISCUSSION_REPLY_IN_APP', 'COURSE', '{}'::jsonb),
    ('system.maintenance.scheduled', true, 'SYSTEM_MAINTENANCE', '["IN_APP","EMAIL"]'::jsonb, 'URGENT', 'IMMEDIATE', 'SYSTEM_MAINTENANCE_EMAIL', 'ALL', '{}'::jsonb)
ON CONFLICT (event_type) DO NOTHING;
