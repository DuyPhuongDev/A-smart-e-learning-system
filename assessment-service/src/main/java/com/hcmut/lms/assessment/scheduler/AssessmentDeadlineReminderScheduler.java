package com.hcmut.lms.assessment.scheduler;

import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.event.AssessmentEventPublisher;
import com.hcmut.lms.assessment.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssessmentDeadlineReminderScheduler {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentEventPublisher assessmentEventPublisher;

    @Value("${assessment.events.deadline-reminder-minutes-before:1440}")
    private long minutesBefore;

    @Value("${assessment.events.deadline-reminder-window-minutes:5}")
    private long windowMinutes;

    @Scheduled(cron = "${assessment.events.deadline-reminder-cron:0 */5 * * * *}")
    @Transactional(readOnly = true)
    public void publishDeadlineReminders() {
        if (minutesBefore <= 0 || windowMinutes <= 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusMinutes(minutesBefore);
        LocalDateTime to = from.plusMinutes(windowMinutes);

        List<Assessment> dueAssessments = assessmentRepository.findByAssessmentStatusAndCloseTimeBetween(
                AssessmentStatus.PUBLISHED,
                from,
                to
        );

        if (dueAssessments.isEmpty()) {
            return;
        }

        for (Assessment assessment : dueAssessments) {
            long minutesToDeadline = Duration.between(now, assessment.getCloseTime()).toMinutes();
            assessmentEventPublisher.publishDeadlineReminder(assessment, minutesToDeadline);
        }

        log.info("Published {} deadline reminder event(s) for window {} -> {}", dueAssessments.size(), from, to);
    }
}
