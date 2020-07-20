package org.example;

import org.example.weatherApi.WeatherApi;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;

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
                    var weather = weatherApi.getWeatherByGeoLocation(location.getLatitude(), location.getLongitude());

                    SendMessage response = new SendMessage()
                            .setText(String.format("Current weather for %s:\n%s %s, %s°C",
                                    weather.locationName,
                                    weatherDescriptionToEmoji(weather.weatherConditions.get(0).description),
                                    weather.weatherConditions.get(0).description,
                                    weather.weatherAttributes.temperature))
                            .setChatId(update.getMessage().getChatId());

                    execute(response);
                }
                catch (Exception e) {
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

    private String weatherDescriptionToEmoji(String description) {
        String emoji = "";

        switch (description) {
            case "clear sky":
                emoji = "\u2600";
                break;

            case "scattered clouds":
                emoji = "\u26C5";
                break;

            case "broken clouds":
                emoji = "\u2601";
                break;

            case "rain":
            case "shower rain":
                emoji = "\uD83C\uDF27";
                break;

            case "thunderstorm":
                emoji = "\u26C8";
                break;

            case "snow":
                emoji = "\uD83C\uDF28";
                break;

            case "mist":
                emoji = "\uD83C\uDF2B";
                break;
        }

        return emoji;
    }
}
