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
                            .setText(String.format("Current weather for %s:\n%s, %s°C", weather.locationName, weather.weatherConditions.get(0).description, weather.weatherAttributes.temperature))
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
}
