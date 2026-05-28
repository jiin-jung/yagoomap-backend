package com.study.yagoomap.domain.place.dto;

import java.util.List;

public record CrawlCandidate(long id, String source, String sourceId, String keyword, String name, String address, String roadAddress, String phone, String mapLink, String categoryName, String categoryGroupCode, double latitude, double longitude, int distanceMeters, String collectedAt, String status, String duplicateReason, String sourceUrl, String duplicateCheckResult, List<String> sourceTeams, String matchScore) {
}
