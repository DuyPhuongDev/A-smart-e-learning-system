ALTER TABLE notification.notification_preferences
    DROP COLUMN IF EXISTS quiet_hours_start,
    DROP COLUMN IF EXISTS quiet_hours_end;
