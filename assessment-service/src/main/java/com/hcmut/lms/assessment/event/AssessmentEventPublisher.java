package com.hcmut.lms.assessment.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssessmentEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${assessment.events.topic-prefix:lms.events}")
    private String topicPrefix;

    @Value("${spring.application.name:assessment-service}")
    private String sourceService;

    public void publishAssignmentCreated(Assessment assessment) {
        if (assessment == null || assessment.getId() == null) {
            return;
        }

        Map<String, Object> data = Map.of(
                "assessmentId", assessment.getId().toString(),
                "classId", assessment.getClassId().toString(),
                "title", nullToEmpty(assessment.getTitle()),
                "assessmentType", assessment.getAssessmentType() == null ? "" : assessment.getAssessmentType().name(),
                "startTime", toIso(assessment.getStartTime()),
                "closeTime", toIso(assessment.getCloseTime())
        );

        publish(
                "assignment.created",
                "assessment.assignment.created",
                assessment.getId().toString(),
                data
        );
    }

    public void publishDeadlineReminder(Assessment assessment, long minutesToDeadline) {
        if (assessment == null || assessment.getId() == null || assessment.getCloseTime() == null) {
            return;
        }

        Map<String, Object> data = Map.of(
                "assessmentId", assessment.getId().toString(),
                "classId", assessment.getClassId().toString(),
                "title", nullToEmpty(assessment.getTitle()),
                "closeTime", toIso(assessment.getCloseTime()),
                "minutesToDeadline", Math.max(minutesToDeadline, 0)
        );

        publish(
                "assignment.deadline_reminder",
                "assessment.assignment.deadline.reminder",
                assessment.getId() + ":deadline:" + assessment.getCloseTime().truncatedTo(ChronoUnit.MINUTES),
                data
        );
    }

    public void publishSubmissionGraded(AssessmentSubmission submission, String gradingStatus) {
        if (submission == null || submission.getId() == null || submission.getAssessment() == null) {
            return;
        }

        Assessment assessment = submission.getAssessment();
        Map<String, Object> data = Map.of(
                "attemptId", submission.getId().toString(),
                "assessmentId", assessment.getId().toString(),
                "classId", assessment.getClassId().toString(),
                "studentId", submission.getStudentId().toString(),
                "title", nullToEmpty(assessment.getTitle()),
                "score", submission.getScore() == null ? "0" : submission.getScore().toPlainString(),
                "submittedAt", submission.getSubmitTime() == null ? Instant.now().toString() : submission.getSubmitTime().toString(),
                "gradingStatus", nullToEmpty(gradingStatus)
        );

        publish(
                "submission.graded",
                "assessment.submission.graded",
                submission.getId().toString(),
                data
        );
    }

    private void publish(String topicSuffix, String eventType, String eventKey, Map<String, Object> data) {
        try {
            LmsEventEnvelope envelope = LmsEventEnvelope.builder()
                    .eventId(eventKey)
                    .eventType(eventType)
                    .occurredAt(Instant.now())
                    .sourceService(sourceService)
                    .version("1.0")
                    .data(data)
                    .build();

            String topic = topicPrefix + "." + topicSuffix;
            String payload = objectMapper.writeValueAsString(envelope);
            kafkaTemplate.send(topic, eventKey, payload).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Failed publishing eventType={} topic={} key={} err={}",
                            eventType, topic, eventKey, throwable.getMessage(), throwable);
                } else {
                    log.debug("Published eventType={} topic={} key={}", eventType, topic, eventKey);
                }
            });
        } catch (Exception ex) {
            log.error("Cannot serialize eventType={} key={}: {}", eventType, eventKey, ex.getMessage(), ex);
        }
    }

    private String toIso(LocalDateTime value) {
        return value == null ? "" : value.toString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
