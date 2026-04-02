package com.hcmut.lms.notification.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        Object userId = attributes.get(UserHandshakeInterceptor.getUserIdAttribute());
        if (userId == null) {
            throw new IllegalStateException("Missing WS user context");
        }
        return new StompPrincipal(userId.toString());
    }
}
