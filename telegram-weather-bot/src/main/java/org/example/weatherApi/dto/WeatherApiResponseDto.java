package org.example.weatherApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class WeatherApiResponseDto {
    public final String locationName;
    public final List<WeatherConditionDto> weatherConditions;
    public final WeatherAttributesDto weatherAttributes;

    public WeatherApiResponseDto(@JsonProperty("name") String locationName,
                                 @JsonProperty("weather") List<WeatherConditionDto> weatherConditions,
                                 @JsonProperty("main") WeatherAttributesDto weatherAttributes) {
        this.locationName = locationName;
        this.weatherConditions = weatherConditions;
        this.weatherAttributes = weatherAttributes;
    }
}
