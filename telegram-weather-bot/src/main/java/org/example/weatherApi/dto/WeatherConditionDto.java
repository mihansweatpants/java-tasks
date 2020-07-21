package org.example.weatherApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherConditionDto {
    public final String name;
    public final String description;
    public final String conditionId;

    public WeatherConditionDto(@JsonProperty("main") String name,
                               @JsonProperty("description") String description,
                               @JsonProperty("id") String conditionId) {
        this.name = name;
        this.description = description;
        this.conditionId = conditionId;
    }
}
