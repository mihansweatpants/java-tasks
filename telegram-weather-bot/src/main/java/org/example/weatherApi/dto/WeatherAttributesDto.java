package org.example.weatherApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherAttributesDto {
    public final Long temperature;
    public final Long feelsLike;
    public final Long pressure;
    public final Long humidity;

    public WeatherAttributesDto(@JsonProperty("temp") Long temperature,
                                @JsonProperty("feels_like") Long feelsLike,
                                @JsonProperty("pressure") Long pressure,
                                @JsonProperty("humidity") Long humidity) {
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.pressure = pressure;
        this.humidity = humidity;
    }
}
