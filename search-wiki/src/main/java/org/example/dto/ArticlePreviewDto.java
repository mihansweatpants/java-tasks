package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticlePreviewDto {
    public final String title;
    public final Integer pageId;
    public final String snippet;

    public ArticlePreviewDto(@JsonProperty("title") String title,
                             @JsonProperty("pageid") Integer pageId,
                             @JsonProperty("snippet") String snippet) {
        this.title = title;
        this.pageId = pageId;
        this.snippet = snippet;
    }
}
