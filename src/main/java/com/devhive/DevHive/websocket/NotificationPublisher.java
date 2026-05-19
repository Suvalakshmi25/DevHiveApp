package com.devhive.DevHive.websocket;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    // 🔔 Send to specific user
    public void sendToUser(Long userId, Object payload) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                payload
        );
    }

    // 🔔 Broadcast to all users
    public void broadcast(Object payload) {
        messagingTemplate.convertAndSend(
                "/topic/notifications",
                payload
        );
    }
}