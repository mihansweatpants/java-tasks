package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ApiResponseDto;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class App {
    private final String SEARCH_API_URL = "https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=";

    private final HttpClient httpClient = HttpClient.newHttpClient();;
    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public ApiResponseDto searchWiki(String query) throws java.net.URISyntaxException, IOException, InterruptedException {
        var uri = new URI(String.format("%s%s", SEARCH_API_URL, URLEncoder.encode(query, StandardCharsets.UTF_8)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode jsonNode = objectMapper.readTree(response.body());

        return objectMapper.readValue(jsonNode.get("query").toString(), ApiResponseDto.class);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Search Wikipedia (\\q to quit): ");

        while(scanner.hasNextLine()) {
            String input = scanner.nextLine();

            if (input.equals("\\q")) {
                break;
            }

            try {
                System.out.printf("\nSearching for '%s'...\n", input);
                var result = searchWiki(input);

                if (result.searchInfo.total == 0) {
                    System.out.println("\nNothing found :(");
                }
                else {
                    System.out.printf("\nFound %d articles\nHere are some of them:\n", result.searchInfo.total);
                    for (var article : result.searchResults) {
                        System.out.printf("- %s\n", article.title);
                    }
                }

            } catch (Exception e) {
                System.out.println("Something went wrong :(");
            }

            System.out.println("\nSearch Wikipedia for something else (\\q to quit): ");
        }
    }
}
