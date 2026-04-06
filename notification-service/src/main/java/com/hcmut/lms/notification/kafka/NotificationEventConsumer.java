package com.hcmut.lms.notification.kafka;

import com.hcmut.lms.notification.service.NotificationApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationApplicationService notificationApplicationService;

    @KafkaListener(
            topics = "${notification.kafka.inbound-topic:lms.events.notification}",
            groupId = "${spring.kafka.consumer.group-id:notification-service}"
    )
    public void consume(ConsumerRecord<String, String> record) {
        if (record.value() == null || record.value().isBlank()) {
            return;
        }

        try {
            notificationApplicationService.handleInboundEvent(record.value());
        } catch (Exception ex) {
            notificationApplicationService.recordInboundFailure(
                    record.topic(),
                    record.key(),
                    record.value(),
                    ex.getMessage()
            );
            log.error(
                    "Failed to process inbound notification event topic={} key={} error={}",
                    record.topic(),
                    record.key(),
                    ex.getMessage(),
                    ex
            );
        }
    }
}
