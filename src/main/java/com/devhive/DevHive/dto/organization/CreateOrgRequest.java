package com.devhive.DevHive.dto.organization;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrgRequest {
    private String name;
}