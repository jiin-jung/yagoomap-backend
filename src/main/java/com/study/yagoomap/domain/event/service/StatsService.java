package com.study.yagoomap.domain.event.service;

import com.study.yagoomap.domain.event.EventType;
import com.study.yagoomap.domain.event.dto.AdminStats;
import com.study.yagoomap.domain.event.repository.EventLogRepository;
import com.study.yagoomap.domain.place.entity.PlaceEntity;
import com.study.yagoomap.domain.place.repository.PlaceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 이벤트 집계(읽기) 전담. 운영 대시보드(GET /api/admin/stats)용 통계를 만든다.
 */
@Service
@Transactional(readOnly = true)
public class StatsService {

    private static final int TOP_LIMIT = 10;
    private static final int TOP_KEYWORD_LIMIT = 20;

    private final EventLogRepository eventLogRepository;
    private final PlaceRepository placeRepository;

    public StatsService(EventLogRepository eventLogRepository, PlaceRepository placeRepository) {
        this.eventLogRepository = eventLogRepository;
        this.placeRepository = placeRepository;
    }

    public AdminStats stats(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime rangeFrom = startOfToday.minusDays(days - 1L);

        return new AdminStats(
                now.toString(),
                days,
                eventLogRepository.count(),
                eventLogRepository.countByCreatedAtGreaterThanEqual(startOfToday),
                eventLogRepository.countByCreatedAtGreaterThanEqual(rangeFrom),
                eventLogRepository.countDistinctSessionsSince(startOfToday),
                eventLogRepository.countDistinctSessionsSince(rangeFrom),
                eventLogRepository.countByTypeAndCreatedAtGreaterThanEqual(EventType.CLICK_KAKAO, rangeFrom),
                eventLogRepository.countByTypeAndCreatedAtGreaterThanEqual(EventType.CLICK_NAVER, rangeFrom),
                eventLogRepository.countByTypeAndCreatedAtGreaterThanEqual(EventType.SUBMIT_REPORT, rangeFrom),
                eventLogRepository.countByTypeAndCreatedAtGreaterThanEqual(EventType.WRITE_REVIEW, rangeFrom),
                byType(rangeFrom),
                topVenues(rangeFrom),
                topKeywords(rangeFrom),
                referrers(rangeFrom),
                dailyTrend(rangeFrom)
        );
    }

    private List<AdminStats.TypeCount> byType(LocalDateTime from) {
        return eventLogRepository.countByTypeSince(from).stream()
                .map(row -> new AdminStats.TypeCount(String.valueOf(row[0]), toLong(row[1])))
                .toList();
    }

    private List<AdminStats.VenueCount> topVenues(LocalDateTime from) {
        List<Object[]> rows = eventLogRepository.topPlacesSince(EventType.VIEW_VENUE, from, PageRequest.of(0, TOP_LIMIT));
        List<Long> placeIds = rows.stream().map(row -> toLong(row[0])).toList();
        Map<Long, String> names = placeRepository.findAllById(placeIds).stream()
                .collect(Collectors.toMap(PlaceEntity::getId, PlaceEntity::getName, (a, b) -> a));
        return rows.stream()
                .map(row -> {
                    long placeId = toLong(row[0]);
                    return new AdminStats.VenueCount(placeId, names.getOrDefault(placeId, "(삭제된 장소)"), toLong(row[1]));
                })
                .toList();
    }

    private List<AdminStats.KeywordCount> topKeywords(LocalDateTime from) {
        return eventLogRepository.topKeywordsSince(EventType.SEARCH, from, PageRequest.of(0, TOP_KEYWORD_LIMIT)).stream()
                .map(row -> new AdminStats.KeywordCount(String.valueOf(row[0]), toLong(row[1])))
                .toList();
    }

    private List<AdminStats.ReferrerCount> referrers(LocalDateTime from) {
        return eventLogRepository.countByReferrerSince(from).stream()
                .map(row -> new AdminStats.ReferrerCount(String.valueOf(row[0]), toLong(row[1])))
                .toList();
    }

    private List<AdminStats.DailyCount> dailyTrend(LocalDateTime from) {
        return eventLogRepository.dailyTrendSince(from).stream()
                .map(row -> new AdminStats.DailyCount(String.valueOf(row[0]), toLong(row[1])))
                .toList();
    }

    private static long toLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }
}
