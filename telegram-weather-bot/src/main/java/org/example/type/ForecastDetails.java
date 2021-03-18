package org.example.type;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ForecastDetails {
    private final Long timestamp;
    private final Long temperature;
    private final Long feelsLike;
    private final List<WeatherCondition> weatherConditions;

    public ForecastDetails(@JsonProperty("dt") Long timestamp,
                           @JsonProperty("temp") Long temperature,
                           @JsonProperty("feels_like") Long feelsLike,
                           @JsonProperty("weather") List<WeatherCondition> weatherConditions) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.weatherConditions = weatherConditions;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getTemperature() {
        return temperature;
    }

    public Long getFeelsLike() {
        return feelsLike;
    }

    public List<WeatherCondition> getWeatherConditions() {
        return weatherConditions;
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s, %s°C",
                weatherConditions.get(0).getEmoji(),
                weatherConditions.get(0).getDescription(),
                temperature
        );
    }
}
