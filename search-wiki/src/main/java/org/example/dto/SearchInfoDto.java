package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchInfoDto {
    public final Integer total;

    public SearchInfoDto(@JsonProperty("totalhits") Integer total) {
        this.total = total;
    }
}
