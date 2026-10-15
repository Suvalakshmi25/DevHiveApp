package com.devhive.DevHive.dto.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteRequest {
    @NotBlank(message = "Email cannot be blank")
    private String email;
    
    @Pattern(regexp = "^(ADMIN|DEVELOPER|VIEWER)$", message = "Role must be ADMIN, DEVELOPER, or VIEWER")
    private String role;
}