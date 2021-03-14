package org.example.wiki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchInfoDto {
    private final Integer total;

    public SearchInfoDto(@JsonProperty("totalhits") Integer total) {
        this.total = total;
    }

    public Integer getTotal() {
        return total;
    }
}
