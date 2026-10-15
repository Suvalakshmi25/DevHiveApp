package com.devhive.DevHive.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NotificationMessage {
    private String type;   // TASK_ASSIGNED, COMMENT_ADDED
    private String message;
    private Long referenceId;
}
