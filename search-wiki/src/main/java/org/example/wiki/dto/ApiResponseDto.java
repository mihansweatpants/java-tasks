package org.example.wiki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ApiResponseDto {
    private final SearchInfoDto searchInfo;
    private final List<ArticlePreviewDto> searchResults;

    public ApiResponseDto(@JsonProperty("searchinfo") SearchInfoDto searchInfo,
                          @JsonProperty("search") List<ArticlePreviewDto> searchResults) {
        this.searchInfo = searchInfo;
        this.searchResults = searchResults;
    }

    public List<ArticlePreviewDto> getSearchResults() {
        return searchResults;
    }

    public SearchInfoDto getSearchInfo() {
        return searchInfo;
    }
}
