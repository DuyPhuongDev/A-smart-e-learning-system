package com.hcmut.lms.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SseNotificationBroadcaster {

    private final Map<UUID, CopyOnWriteArrayList<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    public SseEmitter register(UUID userId) {
        SseEmitter emitter = new SseEmitter(0L);
        CopyOnWriteArrayList<SseEmitter> list = userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());
        list.add(emitter);
        Runnable cleanup = () -> {
            list.remove(emitter);
            if (list.isEmpty()) {
                userEmitters.remove(userId);
            }
        };
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());
        return emitter;
    }

    public void broadcast(UUID userId, Object payload) {
        CopyOnWriteArrayList<SseEmitter> list = userEmitters.get(userId);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(payload));
            } catch (Exception ex) {
                log.debug("SSE push failed userId={}: {}", userId, ex.getMessage());
                list.remove(emitter);
            }
        }
    }

    @Scheduled(fixedRate = 30_000)
    public void heartbeat() {
        for (CopyOnWriteArrayList<SseEmitter> list : userEmitters.values()) {
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event().comment("ping"));
                } catch (Exception ex) {
                    list.remove(emitter);
                }
            }
        }
    }
}
