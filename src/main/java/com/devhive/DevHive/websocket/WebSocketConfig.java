package com.devhive.DevHive.websocket;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 📡 Message Broker (where messages go)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // Clients SUBSCRIBE here
        config.enableSimpleBroker("/topic");

        // Clients SEND messages here
        config.setApplicationDestinationPrefixes("/app");
    }

    // 🔌 WebSocket Endpoint
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")   // 👉 http://localhost:8080/ws
                .setAllowedOrigins("*")
                .withSockJS();       // fallback for browsers
    }

}