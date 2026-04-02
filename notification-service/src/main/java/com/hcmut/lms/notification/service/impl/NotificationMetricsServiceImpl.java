package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.notification.enums.DeliveryStatus;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.service.NotificationMetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class NotificationMetricsServiceImpl implements NotificationMetricsService {

    private final MeterRegistry meterRegistry;
    private final ConcurrentMap<String, AtomicLong> unreadByRole = new ConcurrentHashMap<>();
    private final AtomicLong schedulerLagSeconds = new AtomicLong(0);

    @PostConstruct
    void init() {
        meterRegistry.gauge("notification_scheduler_lag_seconds", schedulerLagSeconds);
    }

    @Override
    public void incrementCreated(NotificationType type) {
        meterRegistry.counter("notifications_created_total", "type", type.name()).increment();
    }

    @Override
    public void incrementDelivery(NotificationChannel channel, DeliveryStatus status) {
        meterRegistry.counter(
                "notification_delivery_total",
                "channel", channel.name(),
                "status", status.name()
        ).increment();
    }

    @Override
    public void recordDeliveryLatency(NotificationChannel channel, long latencyMs) {
        meterRegistry.timer("notification_delivery_latency_ms", "channel", channel.name())
                .record(java.time.Duration.ofMillis(Math.max(latencyMs, 0)));
    }

    @Override
    public void incrementDlq() {
        meterRegistry.counter("notification_dlq_total").increment();
    }

    @Override
    public void setUnreadCount(String role, long value) {
        String safeRole = role == null ? "UNKNOWN" : role.trim().toUpperCase();
        AtomicLong holder = unreadByRole.computeIfAbsent(safeRole, key -> {
            AtomicLong gaugeValue = new AtomicLong(0);
            meterRegistry.gauge("notification_unread_count", io.micrometer.core.instrument.Tags.of("role", key), gaugeValue);
            return gaugeValue;
        });
        holder.set(Math.max(value, 0));
    }

    @Override
    public void setSchedulerLagSeconds(long value) {
        schedulerLagSeconds.set(Math.max(value, 0));
    }
}
