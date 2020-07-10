package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) throws java.net.URISyntaxException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=%22Java%22"))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept((responseBody) -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(responseBody);
                        ApiResponseDto response = objectMapper.readValue(jsonNode.get("query").toString(), ApiResponseDto.class);

                        System.out.println(response.searchInfo.total);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .join();
    }
}

class ApiResponseDto {
    public final SearchInfoDto searchInfo;
    public final List<ArticlePreviewDto> searchResults;

    public ApiResponseDto(@JsonProperty("searchinfo") SearchInfoDto searchInfo,
                          @JsonProperty("search") List<ArticlePreviewDto> searchResults) {
        this.searchInfo = searchInfo;
        this.searchResults = searchResults;
    }
}

class SearchInfoDto {
    public final Integer total;

    public SearchInfoDto(@JsonProperty("totalhits") Integer total) {
        this.total = total;
    }
}

class ArticlePreviewDto {
    public final String title;
    public final Integer pageId;
    public final String snippet;

    public ArticlePreviewDto(@JsonProperty("title") String title,
                             @JsonProperty("pageid") Integer pageId,
                             @JsonProperty("snippet") String snippet) {
        this.title = title;
        this.pageId = pageId;
        this.snippet = snippet;
    }
}
