package com.study.yagoomap.domain.place.service;

import com.study.yagoomap.global.error.ApiException;
import com.study.yagoomap.global.error.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.util.List;
import java.util.function.Function;

@Component
public class KakaoLocalClient {

    private final RestClient restClient;
    private final KakaoLocalProperties properties;

    public KakaoLocalClient(KakaoLocalProperties properties) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("Authorization", "KakaoAK " + properties.restApiKey())
                .build();
        this.properties = properties;
    }

    public KakaoPlaceSearchResponse searchKeyword(KakaoPlaceSearchRequest request) {
        if (properties.restApiKey() == null || properties.restApiKey().isBlank()) {
            throw new ApiException(ErrorCode.KAKAO_API_KEY_MISSING);
        }

        try {
            return restClient.get()
                    .uri(uri(request, "/v2/local/search/keyword.json"))
                    .retrieve()
                    .body(KakaoPlaceSearchResponse.class);
        } catch (RestClientException exception) {
            throw new ApiException(ErrorCode.KAKAO_API_REQUEST_FAILED);
        }
    }

    private Function<UriBuilder, java.net.URI> uri(KakaoPlaceSearchRequest request, String path) {
        return builder -> {
            UriBuilder uri = builder
                    .path(path)
                    .queryParam("query", request.query())
                    .queryParam("size", request.size() == 0 ? 15 : request.size())
                    .queryParam("page", request.page() == 0 ? 1 : request.page())
                    .queryParam("sort", blankToDefault(request.sort(), "accuracy"));

            if (!isBlank(request.categoryGroupCode())) {
                uri.queryParam("category_group_code", request.categoryGroupCode());
            }
            if (request.longitude() != 0 && request.latitude() != 0) {
                uri.queryParam("x", request.longitude());
                uri.queryParam("y", request.latitude());
            }
            if (request.radius() > 0) {
                uri.queryParam("radius", request.radius());
            }
            if (!isBlank(request.rect())) {
                uri.queryParam("rect", request.rect());
            }
            return uri.build();
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String blankToDefault(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }

    public record KakaoPlaceSearchRequest(
            String query,
            String categoryGroupCode,
            double longitude,
            double latitude,
            int radius,
            String rect,
            int page,
            int size,
            String sort
    ) {
    }

    public record KakaoPlaceSearchResponse(
            List<KakaoPlaceDocument> documents,
            KakaoMeta meta
    ) {
    }

    public record KakaoMeta(
            int total_count,
            int pageable_count,
            boolean is_end
    ) {
    }

    public record KakaoPlaceDocument(
            String id,
            String place_name,
            String category_name,
            String category_group_code,
            String category_group_name,
            String phone,
            String address_name,
            String road_address_name,
            String x,
            String y,
            String place_url,
            String distance
    ) {
    }
}
