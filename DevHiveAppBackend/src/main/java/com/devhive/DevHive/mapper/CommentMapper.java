package com.devhive.DevHive.mapper;

import com.devhive.DevHive.dto.comment.CommentResponse;
import com.devhive.DevHive.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .createdAt(comment.getCreatedAt().toString())
                .build();
    }
}