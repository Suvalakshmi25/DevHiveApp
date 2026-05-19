package com.devhive.DevHive.dto.codesnippet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SnippetCommentResponse {
    private Long id;
    private String comment;
    private Integer lineNumber;
    private String userName;
}
