package org.example;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;

public class Main {
    public static void main(String[] args) {
        TelegramBot telegramBot = new TelegramBot(System.getenv("BOT_TOKEN"));
        OpenWeatherMapApiClient openWeatherMapApiClient = new OpenWeatherMapApiClient(System.getenv("OPENWEATHERMAP_API_KEY"));

        UpdatesListener updatesListener = new WeatherBotUpdatesListener(
                new ForecastService(telegramBot, openWeatherMapApiClient),
                new ForecastSubscriptionService(telegramBot, openWeatherMapApiClient)
        );

        telegramBot.setUpdatesListener(updatesListener);
    }
}
