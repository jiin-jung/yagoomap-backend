package com.study.yagoomap.domain.event.dto;

import java.util.List;

/**
 * GET /api/admin/stats 응답. 운영 대시보드용 집계 데이터.
 */
public record AdminStats(
        String generatedAt,
        int rangeDays,
        long totalEvents,
        long eventsToday,
        long eventsInRange,
        long dauToday,
        long activeSessionsInRange,
        long kakaoClicks,
        long naverClicks,
        long reportSubmits,
        long reviewWrites,
        List<TypeCount> byType,
        List<VenueCount> topVenues,
        List<KeywordCount> topKeywords,
        List<ReferrerCount> referrers,
        List<DailyCount> dailyTrend,
        List<DailyCount> dailyDauTrend
) {

    public record TypeCount(String type, long count) {
    }

    public record VenueCount(long placeId, String name, long count) {
    }

    public record KeywordCount(String keyword, long count) {
    }

    public record ReferrerCount(String referrer, long count) {
    }

    public record DailyCount(String date, long count) {
    }
}
