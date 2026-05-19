package com.devhive.DevHive.dto.codesnippet;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSnippetRequest {
    private String title;
    private String code;
    private String language;
    private Long projectId;
}
