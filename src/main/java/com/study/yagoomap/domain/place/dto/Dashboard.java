package com.study.yagoomap.domain.place.dto;

import java.util.List;

public record Dashboard(long placeCount, long reportCount, long pendingReportCount, long reviewCount, List<Place> recentPlaces) {
}
