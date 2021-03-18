package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.type.WeatherApiResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenWeatherMapApiClient {

    private final String apiUriBase;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenWeatherMapApiClient(String apiKey) {
        this.apiUriBase = String.format("https://api.openweathermap.org/data/2.5/onecall?units=metric&appid=%s", apiKey);
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public WeatherApiResponse getWeatherByGeoLocation(float latitude, float longitude) throws IOException, InterruptedException {
        URI uri = URI.create(String.format("%s&lat=%s&lon=%s", apiUriBase, latitude, longitude));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), WeatherApiResponse.class);
    }
}

