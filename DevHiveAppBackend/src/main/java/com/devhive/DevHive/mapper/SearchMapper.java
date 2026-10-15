package com.devhive.DevHive.mapper;

import com.devhive.DevHive.dto.search.SearchResponse;
import com.devhive.DevHive.entity.CodeSnippet;
import com.devhive.DevHive.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class SearchMapper {

    public SearchResponse fromTask(Task task) {
        return SearchResponse.builder()
                .type("TASK")
                .id(task.getId())
                .title(task.getTitle())
                .build();
    }

    public SearchResponse fromSnippet(CodeSnippet snippet) {
        return SearchResponse.builder()
                .type("SNIPPET")
                .id(snippet.getId())
                .title(snippet.getTitle())
                .build();
    }
}