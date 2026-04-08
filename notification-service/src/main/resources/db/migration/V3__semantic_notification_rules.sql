-- Align notification_rules.event_type with SimpleNotificationEvent.semanticType (producer contract)

UPDATE notification.notification_rules
SET event_type = 'ASSIGNMENT_CREATED'
WHERE event_type = 'assessment.assignment.created';

UPDATE notification.notification_rules
SET event_type = 'DEADLINE_REMINDER'
WHERE event_type = 'assessment.assignment.deadline.reminder';

UPDATE notification.notification_rules
SET event_type = 'SUBMISSION_GRADED'
WHERE event_type = 'assessment.submission.graded';

UPDATE notification.notification_rules
SET event_type = 'DISCUSSION_REPLY'
WHERE event_type = 'communication.discussion.reply.created';

UPDATE notification.notification_rules
SET event_type = 'SYSTEM_MAINTENANCE'
WHERE event_type = 'system.maintenance.scheduled';
