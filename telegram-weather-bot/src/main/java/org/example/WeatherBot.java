package org.example;

import org.example.weatherApi.WeatherApi;
import org.example.weatherApi.dto.ForecastDto;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Collectors;

public class WeatherBot extends TelegramLongPollingBot {
    private final WeatherApi weatherApi;

    public WeatherBot(WeatherApi weatherApi) {
        this.weatherApi = weatherApi;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasLocation()) {
                Location location = update.getMessage().getLocation();

                try {
                    sendCurrentForecast(location, update.getMessage().getChatId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "weather-bot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    private void sendCurrentForecast(Location location, Long chatId) throws InterruptedException, IOException, URISyntaxException, TelegramApiException {
        var weatherApiResponse = weatherApi.getWeatherByGeoLocation(location.getLatitude(), location.getLongitude());

        StringBuilder sb = new StringBuilder();

        String currentForecastText = String.format("Current weather for your location:\n%s\n\n", forecastToText(weatherApiResponse.currentForecast));
        sb.append(currentForecastText);

        var clientTimezoneID = ZoneId.of(weatherApiResponse.timezone);

        var forecastForToday = weatherApiResponse.hourlyForecast.stream()
                .filter(forecastDto -> Instant.ofEpochMilli(forecastDto.timestamp * 1000)
                        .atZone(clientTimezoneID)
                        .toLocalDate()
                        .equals(LocalDate.now(clientTimezoneID)))
                .collect(Collectors.toList());

        sb.append("Forecast for the rest of the day:\n");
        forecastForToday.forEach(forecastDto -> {
            String hourlyForecastText = String.format("%s: %s\n",
                    Instant.ofEpochMilli(forecastDto.timestamp * 1000).atZone(clientTimezoneID).toLocalTime(),
                    forecastToText(forecastDto));

            sb.append(hourlyForecastText);
        });

        SendMessage currentForecastResponse = new SendMessage()
                .setText(sb.toString())
                .setChatId(chatId);

        execute(currentForecastResponse);
    }

    private String forecastToText(ForecastDto forecast) {
        return String.format("%s %s, %s°C",
                weatherDescriptionToEmoji(forecast.weatherConditions.get(0).conditionId),
                forecast.weatherConditions.get(0).description,
                forecast.temperature);
    }

    private String weatherDescriptionToEmoji(String weatherConditionId) {
        String emoji = "";

        if (weatherConditionId.equals("800")) {
            emoji = "\u2600";
        } else if (weatherConditionId.startsWith("8")) {
            emoji = "\u2601";
        } else if (weatherConditionId.startsWith("3") || weatherConditionId.startsWith("5")) {
            emoji = "\uD83C\uDF27";
        } else if (weatherConditionId.startsWith("2")) {
            emoji = "\u26C8";
        } else if (weatherConditionId.startsWith("6")) {
            emoji = "\uD83C\uDF28";
        } else if (weatherConditionId.startsWith("7")) {
            emoji = "\uD83C\uDF2B";
        }

        return emoji;
    }
}
