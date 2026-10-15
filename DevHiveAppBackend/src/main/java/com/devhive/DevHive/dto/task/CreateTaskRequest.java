package com.devhive.DevHive.dto.task;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTaskRequest {
    private String title;
    private String description;
    private String priority;
    private Long projectId;
}
