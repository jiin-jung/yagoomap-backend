package com.study.yagoomap.domain.place.dto;

import jakarta.validation.constraints.NotBlank;

public record ReportRequest(
        @NotBlank(message = "장소명은 필수입니다.")
        String placeName,
        @NotBlank(message = "주소는 필수입니다.")
        String address,
        long teamId,
        String team,
        String content,
        String referenceLink
) {
}
