package com.study.yagoomap.domain.place.dto;

public record ReviewSummary(long placeId, double averageRating, int reviewCount, String summary) {
}
