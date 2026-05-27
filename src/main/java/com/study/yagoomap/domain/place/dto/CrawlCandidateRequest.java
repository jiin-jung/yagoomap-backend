package com.study.yagoomap.domain.place.dto;

import jakarta.validation.constraints.NotBlank;

public record CrawlCandidateRequest(
        @NotBlank(message = "키워드는 필수입니다.")
        String keyword,
        @NotBlank(message = "장소명은 필수입니다.")
        String name,
        @NotBlank(message = "주소는 필수입니다.")
        String address,
        String phone,
        String mapLink
) {
}
