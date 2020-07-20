package org.example.weatherApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherConditionDto {
    public final String name;
    public final String description;

    public WeatherConditionDto(@JsonProperty("main") String name,
                                 @JsonProperty("description") String description) {
        this.name = name;
        this.description = description;
    }
}
