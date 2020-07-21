package org.example.weatherApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class WeatherApiResponseDto {
    public final String timezone;
    public final ForecastDto currentForecast;
    public final List<ForecastDto> hourlyForecast;

    public WeatherApiResponseDto(@JsonProperty("timezone") String timezone,
                                 @JsonProperty("current") ForecastDto currentForecast,
                                 @JsonProperty("hourly") List<ForecastDto> hourlyForecast) {
        this.timezone = timezone;
        this.currentForecast = currentForecast;
        this.hourlyForecast = hourlyForecast;
    }
}
