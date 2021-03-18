package org.example;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Location;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.type.WeatherApiResponse;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ForecastSubscriptionService extends ForecastService {
    public static final String SUBSCRIBE_COMMAND = "/subscribe";
    public static final String UNSUBSCRIBE_COMMAND = "/unsubscribe";
    public static final String ASK_FOR_LOCATION_TEXT = "Reply to this message with your location to receive daily forecasts";

    private final Map<Long, ScheduledFuture> subscriptionsByChatId;
    private final ScheduledExecutorService scheduledExecutorService;

    public ForecastSubscriptionService(TelegramBot telegramBot,
                                       OpenWeatherMapApiClient openWeatherMapApiClient) {
        super(telegramBot, openWeatherMapApiClient);
        this.subscriptionsByChatId = new HashMap<>();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void subscribeToDailyForecast(Long chatId, Location location) {
        try {
            WeatherApiResponse weatherApiResponse = openWeatherMapApiClient.getWeatherByGeoLocation(location.latitude(), location.longitude());

            Runnable notificationTask = () -> sendCurrentForecast(chatId, location);

            ZonedDateTime now = ZonedDateTime.now(weatherApiResponse.getTimezoneId());
            ZonedDateTime nextNotificationRunTime = now.withHour(8).withMinute(0).withSecond(0);
            if (now.isAfter(nextNotificationRunTime)) nextNotificationRunTime = nextNotificationRunTime.plusDays(1);

            long initialDelay = Duration.between(now, nextNotificationRunTime).getSeconds();

            ScheduledFuture subscription = scheduledExecutorService.scheduleAtFixedRate(
                    notificationTask,
                    initialDelay,
                    TimeUnit.DAYS.toSeconds(1),
                    TimeUnit.SECONDS
            );

            ScheduledFuture previousSubscription = subscriptionsByChatId.put(chatId, subscription);

            if (previousSubscription != null) {
                previousSubscription.cancel(true);
            }

            telegramBot.execute(new SendMessage(chatId, "You will receive daily forecasts at 8:00 AM"));
        } catch (Exception e) {
            telegramBot.execute(new SendMessage(chatId, "Could not subscribe to daily forecasts:\n" + e.getMessage()));
        }
    }

    public void unsubscribeFromDailyForecast(Long chatId) {
        ScheduledFuture subscription = subscriptionsByChatId.remove(chatId);

        if (subscription != null) {
            subscription.cancel(true);
            telegramBot.execute(new SendMessage(chatId, "You will not receive daily forecasts anymore"));
        } else {
            telegramBot.execute(new SendMessage(chatId, "You are not subscribed to daily forecasts"));
        }
    }

    public void askForLocation(Long chatId) {
        telegramBot.execute(new SendMessage(chatId, ASK_FOR_LOCATION_TEXT));
    }
}
