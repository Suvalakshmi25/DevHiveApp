package com.devhive.DevHive.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devhive.DevHive.dto.notification.NotificationMessage;
import com.devhive.DevHive.dto.notification.NotificationResponse;
import com.devhive.DevHive.entity.Notification;
import com.devhive.DevHive.entity.Task;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.mapper.NotificationMapper;
import com.devhive.DevHive.respository.NotificationRepository;
import com.devhive.DevHive.respository.UserRepository;
import com.devhive.DevHive.websocket.NotificationPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public List<NotificationResponse> getNotifications() {
        User user = getCurrentUser();

        return notificationRepository.findByUser(user).stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return userRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No default user found"));
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + authentication.getName()));
    }
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long userId, String message) {

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                message
        );
    }

    private final NotificationPublisher publisher;


    public void notifyTaskAssigned(User user, Task task) {

        // Save in DB
        Notification notification = Notification.builder()
                .user(user)
                .message("Task assigned: " + task.getTitle())
                .build();

        notificationRepository.save(notification);

        // 🔥 Send real-time message
        NotificationMessage payload = NotificationMessage.builder()
                .type("TASK_ASSIGNED")
                .message(notification.getMessage())
                .referenceId(task.getId())
                .build();

        publisher.sendToUser(user.getId(), payload);
    }
}