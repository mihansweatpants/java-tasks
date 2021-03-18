package org.example;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

public class WeatherBotUpdatesListener implements UpdatesListener {
    private final ForecastService forecastService;
    private final ForecastSubscriptionService forecastSubscriptionService;

    public WeatherBotUpdatesListener(ForecastService forecastService,
                                     ForecastSubscriptionService forecastSubscriptionService) {
        this.forecastService = forecastService;
        this.forecastSubscriptionService = forecastSubscriptionService;
    }

    @Override
    public int process(List<Update> updates) {
        for (var update : updates) {
            var message = update.message();

            if (hasLocation(message)) {
                if (isReplyToLocationPrompt(message)) {
                    forecastSubscriptionService.subscribeToDailyForecast(message.chat().id(), message.location());
                } else {
                    forecastService.sendCurrentForecast(message.chat().id(), message.location());
                }
            } else if (isSubscribeCommand(message)) {
                forecastSubscriptionService.askForLocation(message.chat().id());
            } else if (isUnsubscribeCommand(message)) {
                forecastSubscriptionService.unsubscribeFromDailyForecast(message.chat().id());
            }
        }

        return CONFIRMED_UPDATES_ALL;
    }

    private static boolean isSubscribeCommand(Message message) {
        return hasText(message, ForecastSubscriptionService.SUBSCRIBE_COMMAND);
    }

    private static boolean isUnsubscribeCommand(Message message) {
        return hasText(message, ForecastSubscriptionService.UNSUBSCRIBE_COMMAND);
    }

    private static boolean isReplyToLocationPrompt(Message message) {
        return message.replyToMessage() != null && hasText(message.replyToMessage(), ForecastSubscriptionService.ASK_FOR_LOCATION_TEXT);
    }

    private static boolean hasLocation(Message message) {
        return message != null && message.location() != null;
    }

    private static boolean hasText(Message message, String targetText) {
        return message != null &&
                message.text() != null &&
                message.text().equals(targetText);
    }
}
