package com.study.yagoomap.domain.place.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record BulkCrawlCandidateRequest(
        @NotBlank(message = "수집 출처는 필수입니다.")
        String source,
        @NotBlank(message = "장소명은 필수입니다.")
        String name,
        @NotBlank(message = "주소는 필수입니다.")
        String address,
        double latitude,
        double longitude,
        String phone,
        @JsonAlias("category_name")
        String categoryName,
        @JsonAlias("map_link")
        String mapLink,
        String status,
        String sourceUrl,
        String keyword,
        String collectedAt,
        String duplicateCheckResult,
        @JsonAlias("source_teams")
        List<String> sourceTeams,
        @JsonAlias("match_score")
        String matchScore
) {
}
