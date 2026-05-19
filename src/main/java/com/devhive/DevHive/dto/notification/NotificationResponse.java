package com.devhive.DevHive.dto.notification;

import lombok.*;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private boolean isRead;
    private String createdAt;
}
