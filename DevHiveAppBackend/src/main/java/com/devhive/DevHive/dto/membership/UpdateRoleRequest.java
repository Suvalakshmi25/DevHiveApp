package com.devhive.DevHive.dto.membership;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoleRequest {
    private String role;
}
