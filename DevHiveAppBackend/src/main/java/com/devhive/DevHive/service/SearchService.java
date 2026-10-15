package com.devhive.DevHive.service;

import com.devhive.DevHive.dto.search.SearchResponse;
import com.devhive.DevHive.mapper.SearchMapper;
import com.devhive.DevHive.respository.CodeSnippetRepository;
import com.devhive.DevHive.respository.MembershipRepository;
import com.devhive.DevHive.respository.TaskRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final TaskRepository taskRepository;
    private final CodeSnippetRepository snippetRepository;
    private final SearchMapper searchMapper;
    private final AuthorizationService authorizationService;
    private final MembershipRepository membershipRepository;

    public List<SearchResponse> search(String query) {

        List<SearchResponse> results = new ArrayList<>();
        Set<Long> organizationIds = membershipRepository.findByUser(authorizationService.currentUser())
                .stream()
                .map(membership -> membership.getOrganization().getId())
                .collect(Collectors.toSet());

        results.addAll(taskRepository.findByTitleContainingIgnoreCase(query)
                .stream()
                .filter(task -> organizationIds.contains(task.getProject().getOrganization().getId()))
                .map(searchMapper::fromTask)
                .toList());

        results.addAll(snippetRepository.findByTitleContainingIgnoreCase(query)
                .stream()
                .filter(snippet -> organizationIds.contains(snippet.getProject().getOrganization().getId()))
                .map(searchMapper::fromSnippet)
                .toList());

        return results;
    }
}