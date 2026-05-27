package com.study.yagoomap.domain.place.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.yagoomap.global.error.ApiException;
import com.study.yagoomap.global.error.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class NaverImageClient {

    private static final int DEFAULT_DISPLAY = 3;
    private static final String DEFAULT_SORT = "sim";
    private static final String DEFAULT_FILTER = "large";

    private final RestClient restClient;
    private final NaverImageProperties properties;

    public NaverImageClient(NaverImageProperties properties) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
        this.properties = properties;
    }

    public ImageSearchResponse search(String query) {
        if (isBlank(properties.clientId()) || isBlank(properties.clientSecret())) {
            throw new ApiException(ErrorCode.NAVER_API_KEY_MISSING);
        }

        try {
            NaverImageSearchResponse response = restClient.get()
                    .uri(builder -> builder
                            .path("/v1/search/image")
                            .queryParam("query", query)
                            .queryParam("display", DEFAULT_DISPLAY)
                            .queryParam("sort", DEFAULT_SORT)
                            .queryParam("filter", DEFAULT_FILTER)
                            .build())
                    .header("X-Naver-Client-Id", properties.clientId())
                    .header("X-Naver-Client-Secret", properties.clientSecret())
                    .retrieve()
                    .body(NaverImageSearchResponse.class);

            List<Image> images = response == null || response.items() == null
                    ? List.of()
                    : response.items().stream()
                    .map(item -> new Image(item.title(), item.link(), item.thumbnail(), item.sizeWidth(), item.sizeHeight()))
                    .toList();
            return new ImageSearchResponse(query, images);
        } catch (RestClientException exception) {
            throw new ApiException(ErrorCode.NAVER_API_REQUEST_FAILED);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record ImageSearchResponse(
            String query,
            List<Image> images
    ) {
    }

    public record Image(
            String title,
            String link,
            String thumbnail,
            String sizeWidth,
            String sizeHeight
    ) {
    }

    private record NaverImageSearchResponse(
            List<NaverImageItem> items
    ) {
    }

    private record NaverImageItem(
            String title,
            String link,
            String thumbnail,
            @JsonProperty("sizewidth") String sizeWidth,
            @JsonProperty("sizeheight") String sizeHeight
    ) {
    }
}
