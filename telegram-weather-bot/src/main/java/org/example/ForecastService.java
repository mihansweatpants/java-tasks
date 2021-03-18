package org.example;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Location;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.type.ForecastDetails;
import org.example.type.WeatherApiResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class ForecastService {

    protected final TelegramBot telegramBot;
    protected final OpenWeatherMapApiClient openWeatherMapApiClient;

    public ForecastService(TelegramBot telegramBot,
                           OpenWeatherMapApiClient openWeatherMapApiClient) {
        this.telegramBot = telegramBot;
        this.openWeatherMapApiClient = openWeatherMapApiClient;
    }

    public void sendCurrentForecast(Long chatId, Location location) {
        try {
            WeatherApiResponse weatherApiResponse = openWeatherMapApiClient.getWeatherByGeoLocation(location.latitude(), location.longitude());

            StringBuilder sb = new StringBuilder("Current weather for your location:\n");

            sb.append(weatherApiResponse.getCurrentForecast()).append("\n");

            List<ForecastDetails> forecastForToday = weatherApiResponse.getHourlyForecast()
                    .stream()
                    .filter(forecastDetails ->
                            Instant.ofEpochSecond(forecastDetails.getTimestamp())
                                    .atZone(weatherApiResponse.getTimezoneId())
                                    .toLocalDate()
                                    .equals(LocalDate.now(weatherApiResponse.getTimezoneId())))
                    .collect(Collectors.toList());

            sb.append("Forecast for the rest of the day:\n");
            forecastForToday.forEach(forecastDetails -> {
                LocalTime forecastTime = Instant.ofEpochSecond(forecastDetails.getTimestamp())
                        .atZone(weatherApiResponse.getTimezoneId())
                        .toLocalTime();

                sb.append(forecastTime).append(": ").append(forecastDetails).append("\n");
            });

            telegramBot.execute(new SendMessage(chatId, sb.toString()));
        }
        catch (Exception e) {
            telegramBot.execute(new SendMessage(chatId, "Could not send current forecast:\n" + e.getMessage()));
        }
    }
}
