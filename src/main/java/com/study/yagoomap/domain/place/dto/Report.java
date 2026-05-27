package com.study.yagoomap.domain.place.dto;

public record Report(long id, String placeName, String address, long teamId, String team, String content, String referenceLink, String createdAt, String status, String rejectReason) {
}
