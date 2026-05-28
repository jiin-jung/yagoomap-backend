package com.study.yagoomap.domain.place.dto;

import java.util.List;

public record Place(
        long id,
        String kakaoPlaceId,
        String name,
        String team,
        long teamId,
        String district,
        String address,
        String roadAddress,
        double latitude,
        double longitude,
        String category,
        String categoryName,
        String categoryGroupCode,
        String phone,
        String kakaoPlaceUrl,
        String representativeImageUrl,
        String note,
        String status,
        double rating,
        int reviewCount,
        int distanceMeters,
        List<String> tags,
        String createdAt,
        String updatedAt
) {
}
