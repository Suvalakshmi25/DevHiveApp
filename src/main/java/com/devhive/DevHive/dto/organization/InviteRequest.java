package com.devhive.DevHive.dto.organization;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteRequest {
    private String email;
    private String role; // ADMIN / DEVELOPER / VIEWER
}