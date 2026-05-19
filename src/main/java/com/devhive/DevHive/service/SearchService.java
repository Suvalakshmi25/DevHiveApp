package com.devhive.DevHive.service;

import com.devhive.DevHive.dto.search.SearchResponse;
import com.devhive.DevHive.mapper.SearchMapper;
import com.devhive.DevHive.respository.CodeSnippetRepository;
import com.devhive.DevHive.respository.TaskRepository;
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

    public List<SearchResponse> search(String query) {

        List<SearchResponse> results = new ArrayList<>();

        results.addAll(taskRepository.findByTitleContainingIgnoreCase(query)
                .stream().map(searchMapper::fromTask).toList());

        results.addAll(snippetRepository.findByTitleContainingIgnoreCase(query)
                .stream().map(searchMapper::fromSnippet).toList());

        return results;
    }
}