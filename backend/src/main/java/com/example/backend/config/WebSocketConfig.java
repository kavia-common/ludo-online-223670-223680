package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket and STOMP configuration.
 *
 * PUBLIC_INTERFACE
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.ws.endpoint:/ws}")
    private String wsEndpoint;

    // PUBLIC_INTERFACE
    /**
     * Configure STOMP endpoints for client connections.
     * Registers SockJS fallback and allows CORS to be configured at proxy level.
     *
     * @param registry StompEndpointRegistry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(StringUtils.hasText(wsEndpoint) ? wsEndpoint : "/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    // PUBLIC_INTERFACE
    /**
     * Configure message broker with application destination prefix and simple broker.
     *
     * - Clients send to /app/**
     * - Server broadcasts to /topic/**
     *
     * @param registry MessageBrokerRegistry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }
}
