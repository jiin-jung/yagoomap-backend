package com.study.yagoomap.domain.place.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record KakaoCollectRequest(
        @NotBlank(message = "검색어는 필수입니다.")
        String query,
        String categoryGroupCode,
        double longitude,
        double latitude,
        @Min(value = 0, message = "반경은 0 이상이어야 합니다.")
        @Max(value = 20000, message = "반경은 최대 20000m까지 가능합니다.")
        int radius,
        String rect,
        @Min(value = 0, message = "페이지는 0 또는 1 이상이어야 합니다.")
        @Max(value = 45, message = "페이지는 45 이하이어야 합니다.")
        int page,
        @Min(value = 0, message = "페이지 크기는 0 또는 1 이상이어야 합니다.")
        @Max(value = 15, message = "페이지 크기는 15 이하이어야 합니다.")
        int size,
        String sort
) {
}
