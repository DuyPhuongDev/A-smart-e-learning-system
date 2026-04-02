package com.hcmut.lms.notification.config;

import com.hcmut.lms.notification.websocket.UserHandshakeHandler;
import com.hcmut.lms.notification.websocket.UserHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class NotificationWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final NotificationProperties notificationProperties;
    private final UserHandshakeInterceptor userHandshakeInterceptor;
    private final UserHandshakeHandler userHandshakeHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] allowedOriginPatterns = notificationProperties.getWebsocket().getAllowedOrigins() == null
                ? new String[]{"http://localhost:3000"}
                : notificationProperties.getWebsocket().getAllowedOrigins().toArray(String[]::new);
        registry
                .addEndpoint("/ws/notifications")
                .setHandshakeHandler(userHandshakeHandler)
                .addInterceptors(userHandshakeInterceptor)
                .setAllowedOriginPatterns(allowedOriginPatterns);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue", "/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}
