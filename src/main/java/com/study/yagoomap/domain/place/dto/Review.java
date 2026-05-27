package com.study.yagoomap.domain.place.dto;

public record Review(long id, long placeId, String content, int rating, String createdAt, boolean active) {
}
