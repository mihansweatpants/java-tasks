package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ApiResponseDto {
    public final SearchInfoDto searchInfo;
    public final List<ArticlePreviewDto> searchResults;

    public ApiResponseDto(@JsonProperty("searchinfo") SearchInfoDto searchInfo,
                          @JsonProperty("search") List<ArticlePreviewDto> searchResults) {
        this.searchInfo = searchInfo;
        this.searchResults = searchResults;
    }
}
