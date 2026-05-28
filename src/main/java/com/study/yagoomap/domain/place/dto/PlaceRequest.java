package com.study.yagoomap.domain.place.dto;

import java.util.List;

public record PlaceRequest(
        String kakaoPlaceId,
        String name,
        String address,
        double latitude,
        double longitude,
        long teamId,
        String team,
        String category,
        String categoryName,
        String categoryGroupCode,
        String phone,
        String kakaoPlaceUrl,
        String note,
        List<String> tags,
        String status
) {
    public PlaceRequest(String name, String address, double latitude, double longitude, long teamId, String team, String category, String phone, String note, List<String> tags, String status) {
        this(null, name, address, latitude, longitude, teamId, team, category, null, null, phone, null, note, tags, status);
    }

    public String roadAddress() {
        return null;
    }
}
