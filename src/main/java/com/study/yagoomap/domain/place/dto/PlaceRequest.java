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
        String instagramUrl,
        String naverMapUrl,
        List<String> photos,
        String note,
        List<String> tags,
        String status
) {
    public PlaceRequest(String name, String address, double latitude, double longitude, long teamId, String team, String category, String phone, String instagramUrl, String naverMapUrl, List<String> photos, String note, List<String> tags, String status) {
        this(null, name, address, latitude, longitude, teamId, team, category, null, null, phone, instagramUrl, naverMapUrl, photos, note, tags, status);
    }

    public String roadAddress() {
        return null;
    }

    public String kakaoPlaceUrl() {
        return null;
    }
}
