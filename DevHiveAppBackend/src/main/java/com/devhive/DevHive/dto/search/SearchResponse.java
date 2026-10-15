package com.devhive.DevHive.dto.search;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {
    private String type; // TASK / COMMENT / SNIPPET
    private Long id;
    private String title;
}