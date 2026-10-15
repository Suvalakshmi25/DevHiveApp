package com.devhive.DevHive.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devhive.DevHive.dto.codesnippet.CreateSnippetRequest;
import com.devhive.DevHive.dto.codesnippet.SnippetCommentRequest;
import com.devhive.DevHive.dto.codesnippet.SnippetCommentResponse;
import com.devhive.DevHive.dto.codesnippet.SnippetResponse;
import com.devhive.DevHive.entity.CodeSnippet;
import com.devhive.DevHive.entity.Project;
import com.devhive.DevHive.entity.SnippetComment;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.enums.Role;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.mapper.SnippetMapper;
import com.devhive.DevHive.respository.CodeSnippetRepository;
import com.devhive.DevHive.respository.SnippetCommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnippetService {

    private final CodeSnippetRepository snippetRepository;
    private final SnippetCommentRepository commentRepository;
    private final SnippetMapper snippetMapper;
    private final AuthorizationService authorizationService;

    public SnippetResponse createSnippet(CreateSnippetRequest request) {

        Project project = authorizationService.requireProjectRole(
                request.getProjectId(), Role.ADMIN, Role.DEVELOPER);
        User user = authorizationService.currentUser();

        CodeSnippet snippet = CodeSnippet.builder()
                .title(request.getTitle())
                .code(request.getCode())
                .language(request.getLanguage())
                .project(project)
                .createdBy(user)
                .build();

        snippetRepository.save(snippet);

        return snippetMapper.toResponse(snippet);
    }

    public List<SnippetResponse> getSnippets(Long projectId) {
        authorizationService.requireProjectRole(projectId, Role.ADMIN, Role.DEVELOPER, Role.VIEWER);
        return snippetRepository.findByProjectId(projectId).stream()
                .map(snippetMapper::toResponse)
                .toList();
    }

    public SnippetCommentResponse addComment(Long id, SnippetCommentRequest request) {

        authorizationService.requireSnippetRole(id, Role.ADMIN, Role.DEVELOPER);
        User user = authorizationService.currentUser();
        CodeSnippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CodeSnippet", "id", id));

        SnippetComment comment = SnippetComment.builder()
                .snippet(snippet)
                .user(user)
                .comment(request.getComment())
                .lineNumber(request.getLineNumber())
                .build();

        commentRepository.save(comment);

        return snippetMapper.toCommentResponse(comment);
    }

    public List<SnippetCommentResponse> getComments(Long id) {
        authorizationService.requireSnippetRole(id, Role.ADMIN, Role.DEVELOPER, Role.VIEWER);
        return commentRepository.findBySnippetId(id).stream()
                .map(snippetMapper::toCommentResponse)
                .toList();
    }

}