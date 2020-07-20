package org.example.weatherApi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.weatherApi.dto.WeatherApiResponseDto;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherApi {
    private final String apiUriBase;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public WeatherApi() {
        this.apiUriBase = String.format("https://api.openweathermap.org/data/2.5/weather?units=metric&appid=%s",
                                        System.getenv("OPENWEATHERMAP_API_KEY"));
    }

    public WeatherApiResponseDto getWeatherByGeoLocation(float latitude, float longitude) throws URISyntaxException, IOException, InterruptedException {
        URI uri = new URI(String.format("%s&lat=%s&lon=%s", apiUriBase, latitude, longitude));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), WeatherApiResponseDto.class);
    }
}

