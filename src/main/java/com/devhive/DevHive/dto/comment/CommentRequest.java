package com.devhive.DevHive.dto.comment;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {
    private String content;
}
