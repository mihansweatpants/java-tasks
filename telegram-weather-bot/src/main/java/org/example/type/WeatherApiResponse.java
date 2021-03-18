package org.example.type;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZoneId;
import java.util.List;

public class WeatherApiResponse {
    private final ZoneId timezoneId;
    private final ForecastDetails currentForecastDetails;
    private final List<ForecastDetails> hourlyForecastDetails;

    public WeatherApiResponse(@JsonProperty("timezone") String timezone,
                              @JsonProperty("current") ForecastDetails currentForecastDetails,
                              @JsonProperty("hourly") List<ForecastDetails> hourlyForecastDetails) {
        this.timezoneId = ZoneId.of(timezone);
        this.currentForecastDetails = currentForecastDetails;
        this.hourlyForecastDetails = hourlyForecastDetails;
    }

    public ForecastDetails getCurrentForecast() {
        return currentForecastDetails;
    }

    public List<ForecastDetails> getHourlyForecast() {
        return hourlyForecastDetails;
    }

    public ZoneId getTimezoneId() {
        return timezoneId;
    }
}
