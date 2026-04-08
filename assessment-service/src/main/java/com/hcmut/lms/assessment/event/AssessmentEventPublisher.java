package com.hcmut.lms.assessment.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmission;
import com.hcmut.lms.common.event.NotificationTargetType;
import com.hcmut.lms.common.event.SimpleNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssessmentEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${notification.events.topic:lms.events.notification}")
    private String notificationTopic;

    @Value("${spring.application.name:assessment-service}")
    private String sourceService;

    public void publishAssignmentCreated(Assessment assessment) {
        if (assessment == null || assessment.getId() == null) {
            return;
        }

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("assessmentId", assessment.getId().toString());
        metadata.put("classId", assessment.getClassId().toString());
        metadata.put("title", nullToEmpty(assessment.getTitle()));
        metadata.put("assessmentType", assessment.getAssessmentType() == null ? "" : assessment.getAssessmentType().name());
        metadata.put("startTime", toIso(assessment.getStartTime()));
        metadata.put("closeTime", toIso(assessment.getCloseTime()));

        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .messageId(assessment.getId().toString())
                .sourceService(sourceService)
                .semanticType("ASSIGNMENT_CREATED")
                .targetType(NotificationTargetType.CLASS)
                .targetId(assessment.getClassId().toString())
                .title("Bài tập mới")
                .content("Một bài tập mới đã được tạo cho lớp của bạn: " + nullToEmpty(assessment.getTitle()))
                .metadata(metadata)
                .build();

        publish(event);
    }

    public void publishDeadlineReminder(Assessment assessment, long minutesToDeadline) {
        if (assessment == null || assessment.getId() == null || assessment.getCloseTime() == null) {
            return;
        }

        String messageId = assessment.getId() + ":deadline:" + assessment.getCloseTime().truncatedTo(ChronoUnit.MINUTES);

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("assessmentId", assessment.getId().toString());
        metadata.put("classId", assessment.getClassId().toString());
        metadata.put("title", nullToEmpty(assessment.getTitle()));
        metadata.put("closeTime", toIso(assessment.getCloseTime()));
        metadata.put("minutesToDeadline", Math.max(minutesToDeadline, 0));

        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .messageId(messageId)
                .sourceService(sourceService)
                .semanticType("DEADLINE_REMINDER")
                .targetType(NotificationTargetType.CLASS)
                .targetId(assessment.getClassId().toString())
                .title("Nhắc hạn nộp bài")
                .content("Bài tập \"" + nullToEmpty(assessment.getTitle()) + "\" sắp đến hạn nộp.")
                .metadata(metadata)
                .build();

        publish(event);
    }

    public void publishSubmissionGraded(AssessmentSubmission submission, String gradingStatus) {
        if (submission == null || submission.getId() == null || submission.getAssessment() == null) {
            return;
        }

        Assessment assessment = submission.getAssessment();
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("attemptId", submission.getId().toString());
        metadata.put("assessmentId", assessment.getId().toString());
        metadata.put("classId", assessment.getClassId().toString());
        metadata.put("studentId", submission.getStudentId().toString());
        metadata.put("title", nullToEmpty(assessment.getTitle()));
        metadata.put("score", submission.getScore() == null ? "0" : submission.getScore().toPlainString());
        metadata.put("submittedAt", submission.getSubmitTime() == null ? Instant.now().toString() : submission.getSubmitTime().toString());
        metadata.put("gradingStatus", nullToEmpty(gradingStatus));

        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .messageId(submission.getId().toString())
                .sourceService(sourceService)
                .semanticType("SUBMISSION_GRADED")
                .targetType(NotificationTargetType.USER)
                .targetId(submission.getStudentId().toString())
                .title("Bài nộp đã được chấm")
                .content("Bài tập \"" + nullToEmpty(assessment.getTitle()) + "\" đã được chấm điểm.")
                .metadata(metadata)
                .build();

        publish(event);
    }

    private void publish(SimpleNotificationEvent event) {
        try {
            String key = event.getMessageId();
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(notificationTopic, key, payload).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Failed publishing semanticType={} topic={} key={} err={}",
                            event.getSemanticType(), notificationTopic, key, throwable.getMessage(), throwable);
                } else {
                    log.debug("Published semanticType={} topic={} key={}", event.getSemanticType(), notificationTopic, key);
                }
            });
        } catch (Exception ex) {
            log.error("Cannot serialize notification key={}: {}", event.getMessageId(), ex.getMessage(), ex);
        }
    }

    private String toIso(LocalDateTime value) {
        return value == null ? "" : value.toString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
