package com.devhive.DevHive.mapper;

import com.devhive.DevHive.dto.notification.NotificationResponse;
import com.devhive.DevHive.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt().toString())
                .build();
    }
}