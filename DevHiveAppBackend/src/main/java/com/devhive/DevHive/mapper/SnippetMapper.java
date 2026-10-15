package com.devhive.DevHive.mapper;

import com.devhive.DevHive.dto.codesnippet.SnippetCommentResponse;
import com.devhive.DevHive.dto.codesnippet.SnippetResponse;
import com.devhive.DevHive.entity.CodeSnippet;
import com.devhive.DevHive.entity.SnippetComment;
import org.springframework.stereotype.Component;

@Component
public class SnippetMapper {

    public SnippetResponse toResponse(CodeSnippet snippet) {
        return SnippetResponse.builder()
                .id(snippet.getId())
                .title(snippet.getTitle())
                .code(snippet.getCode())
                .language(snippet.getLanguage())
                .build();
    }

    public SnippetCommentResponse toCommentResponse(SnippetComment comment) {
        return SnippetCommentResponse.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .lineNumber(comment.getLineNumber())
                .userName(comment.getUser().getName())
                .build();
    }
}