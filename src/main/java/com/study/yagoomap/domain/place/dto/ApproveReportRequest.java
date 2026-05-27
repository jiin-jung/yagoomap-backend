package com.study.yagoomap.domain.place.dto;

public record ApproveReportRequest(String name, String address, double latitude, double longitude, long teamId, String team) {
}
