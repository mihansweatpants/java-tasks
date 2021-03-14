package org.example.wiki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticlePreviewDto {
    private final String title;
    private final Integer pageId;
    private final String snippet;

    public ArticlePreviewDto(@JsonProperty("title") String title,
                             @JsonProperty("pageid") Integer pageId,
                             @JsonProperty("snippet") String snippet) {
        this.title = title;
        this.pageId = pageId;
        this.snippet = snippet;
    }

    public Integer getPageId() {
        return pageId;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getTitle() {
        return title;
    }
}
