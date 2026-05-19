package com.devhive.DevHive.service;

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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
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