package com.devhive.DevHive.dto.project;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectRequest {
    private String name;
    private Long organizationId;
}
