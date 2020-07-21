package org.example.weatherApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ForecastDto {
    public final Long timestamp;
    public final Long temperature;
    public final Long feelsLike;
    public final List<WeatherConditionDto> weatherConditions;

    public ForecastDto(@JsonProperty("dt") Long timestamp,
                       @JsonProperty("temp") Long temperature,
                       @JsonProperty("feels_like") Long feelsLike,
                       @JsonProperty("weather") List<WeatherConditionDto> weatherConditions) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.weatherConditions = weatherConditions;
    }
}
