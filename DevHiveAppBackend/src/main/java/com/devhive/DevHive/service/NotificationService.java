package com.devhive.DevHive.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.devhive.DevHive.dto.notification.NotificationMessage;
import com.devhive.DevHive.dto.notification.NotificationResponse;
import com.devhive.DevHive.entity.Notification;
import com.devhive.DevHive.entity.Task;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.mapper.NotificationMapper;
import com.devhive.DevHive.respository.NotificationRepository;
import com.devhive.DevHive.websocket.NotificationPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final AuthorizationService authorizationService;

    public List<NotificationResponse> getNotifications() {
        User user = authorizationService.currentUser();

        return notificationRepository.findByUser(user).stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    public void markAsRead(Long id) {
        User user = authorizationService.currentUser();
        Notification notification = notificationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notification.setRead(true);
        notificationRepository.save(notification);
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