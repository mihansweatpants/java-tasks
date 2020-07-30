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
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WeatherBot extends TelegramLongPollingBot {
    private final String SUBSCRIBE_COMMAND = "/subscribe";
    private final String UNSUBSCRIBE_COMMAND = "/unsubscribe";
    private final String ASK_FOR_LOCATION_TEXT = "Please reply to this message with your location";

    private final WeatherApi weatherApi;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<ForecastSubscription> subscriptions = new ArrayList<>();

    public WeatherBot(WeatherApi weatherApi) {
        this.weatherApi = weatherApi;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            try {
                if (update.getMessage().isCommand()) {
                    if (update.getMessage().getText().equals(SUBSCRIBE_COMMAND)) {
                        subscribeToForecasts(update.getMessage().getChatId());
                        return;
                    }

                    if (update.getMessage().getText().equals(UNSUBSCRIBE_COMMAND)) {
                        unsubscribeFromForecasts(update.getMessage().getChatId());
                        return;
                    }
                }

                if (update.getMessage().hasLocation()) {
                    if (update.getMessage().isReply() && update.getMessage().getReplyToMessage().getText().equals(ASK_FOR_LOCATION_TEXT)) {
                        subscribeToForecasts(update.getMessage().getChatId(), update.getMessage().getLocation());
                        return;
                    }

                    sendCurrentForecast(update.getMessage().getChatId(), update.getMessage().getLocation());
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    private void subscribeToForecasts(Long chatId) throws TelegramApiException {
        if (isSubscriptionStillActive(chatId)) {
            execute(new SendMessage(chatId, "You are already subscribed.\nUse /unsubscribe command first if you want to update your subscription."));
            return;
        }

        execute(new SendMessage(chatId, ASK_FOR_LOCATION_TEXT));
    }

    private void subscribeToForecasts(Long chatId, Location location) throws InterruptedException, IOException, URISyntaxException, TelegramApiException {
        var weatherApiResponse = weatherApi.getWeatherByGeoLocation(location.getLatitude(), location.getLongitude());

        ForecastSubscription subscription = new ForecastSubscription(chatId, location, ZoneId.of(weatherApiResponse.timezone));
        subscriptions.add(subscription);

        scheduleNotifications(subscription);

        execute(new SendMessage(chatId, "You will receive daily forecasts"));
    }

    private boolean isSubscriptionStillActive(Long chatId) {
        return subscriptions.stream().anyMatch(subscription -> subscription.getChatId().equals(chatId));
    }

    private void unsubscribeFromForecasts(Long chatId) throws TelegramApiException {
        removeSubscription(chatId);
        execute(new SendMessage(chatId, "You will not receive daily forecasts anymore"));
    }

    private void scheduleNotifications(ForecastSubscription subscription) {
        Runnable forecastNotificationTask = () -> {
            try {
                sendCurrentForecast(subscription.getChatId(), subscription.getLocation());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        ZonedDateTime now = ZonedDateTime.now(subscription.getTimezoneId());
        ZonedDateTime nextRun = now.withHour(8).withMinute(0).withSecond(0);
        if (now.compareTo(nextRun) > 0) nextRun = nextRun.plusDays(1);

        long initialDelay = Duration.between(now, nextRun).getSeconds();

        subscription.setScheduledNotificationTask(
                scheduler.scheduleAtFixedRate(
                        forecastNotificationTask,
                        initialDelay,
                        TimeUnit.DAYS.toSeconds(1),
                        TimeUnit.SECONDS
                )
        );
    }

    private void removeSubscription(Long chatId) {
        var subscriptionToRemove = subscriptions.stream()
                .filter(subscription -> subscription.getChatId().equals(chatId))
                .findAny()
                .get();

        subscriptionToRemove.getScheduledNotificationTask().cancel(true);

        subscriptions.remove(subscriptionToRemove);
    }

    private void sendCurrentForecast(Long chatId, Location location) throws InterruptedException, IOException, URISyntaxException, TelegramApiException {
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

class ForecastSubscription {
    private final Long chatId;
    private final Location location;
    private final ZoneId timezoneId;

    private Future scheduledNotificationTask;

    public ForecastSubscription(Long chatId, Location location, ZoneId timezoneId) {
        this.chatId = chatId;
        this.location = location;
        this.timezoneId = timezoneId;
    }

    public void setScheduledNotificationTask(Future scheduledNotificationTask) {
        this.scheduledNotificationTask = scheduledNotificationTask;
    }

    public Future getScheduledNotificationTask() {
        return scheduledNotificationTask;
    }

    public Location getLocation() {
        return location;
    }

    public Long getChatId() {
        return chatId;
    }

    public ZoneId getTimezoneId() {
        return timezoneId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForecastSubscription that = (ForecastSubscription) o;
        return Objects.equals(chatId, that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, location, timezoneId, scheduledNotificationTask);
    }
}

