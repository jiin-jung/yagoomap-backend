package com.study.yagoomap.domain.place.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(
        @NotBlank(message = "리뷰 내용은 필수입니다.")
        String content,
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5점 이하이어야 합니다.")
        int rating
) {
}
