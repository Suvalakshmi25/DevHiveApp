package com.devhive.DevHive.dto.codesnippet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SnippetCommentRequest {
    private String comment;
    private Integer lineNumber;
}
