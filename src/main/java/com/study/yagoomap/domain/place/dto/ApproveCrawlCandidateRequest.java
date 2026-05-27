package com.study.yagoomap.domain.place.dto;

import java.util.List;

public record ApproveCrawlCandidateRequest(String name, String address, double latitude, double longitude, long teamId, String team, String note, List<String> tags) {
}
