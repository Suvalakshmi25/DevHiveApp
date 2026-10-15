package com.devhive.DevHive.dto.codesnippet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SnippetResponse {
    private Long id;
    private String title;
    private String code;
    private String language;
}
