package com.devhive.DevHive.service;

import com.devhive.DevHive.dto.codesnippet.CreateSnippetRequest;
import com.devhive.DevHive.dto.codesnippet.SnippetCommentRequest;
import com.devhive.DevHive.dto.codesnippet.SnippetCommentResponse;
import com.devhive.DevHive.dto.codesnippet.SnippetResponse;
import com.devhive.DevHive.entity.CodeSnippet;
import com.devhive.DevHive.entity.Project;
import com.devhive.DevHive.entity.SnippetComment;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.mapper.SnippetMapper;
import com.devhive.DevHive.respository.CodeSnippetRepository;
import com.devhive.DevHive.respository.ProjectRepository;
import com.devhive.DevHive.respository.SnippetCommentRepository;
import com.devhive.DevHive.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SnippetService {

    private final CodeSnippetRepository snippetRepository;
    private final SnippetCommentRepository commentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SnippetMapper snippetMapper;

    public SnippetResponse createSnippet(CreateSnippetRequest request) {

        User user = getCurrentUser();

        Project project = projectRepository.findById(request.getProjectId()).orElseThrow();

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
        return snippetRepository.findByProjectId(projectId).stream()
                .map(snippetMapper::toResponse)
                .toList();
    }

    public SnippetCommentResponse addComment(Long id, SnippetCommentRequest request) {

        User user = getCurrentUser();

        CodeSnippet snippet = snippetRepository.findById(id).orElseThrow();

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
        return commentRepository.findBySnippetId(id).stream()
                .map(snippetMapper::toCommentResponse)
                .toList();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
    }
}