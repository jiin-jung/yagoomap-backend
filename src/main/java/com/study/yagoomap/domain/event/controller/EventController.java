package com.study.yagoomap.domain.event.controller;

import com.study.yagoomap.domain.event.dto.AdminStats;
import com.study.yagoomap.domain.event.dto.EventRequest;
import com.study.yagoomap.domain.event.service.EventService;
import com.study.yagoomap.domain.event.service.StatsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class EventController {

    private final EventService eventService;
    private final StatsService statsService;

    public EventController(EventService eventService, StatsService statsService) {
        this.eventService = eventService;
        this.statsService = statsService;
    }

    /** 사용자 행동 이벤트 수집 — fire-and-forget. 응답 본문 없이 즉시 202 반환. */
    @PostMapping("/api/events")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void collect(@Valid @RequestBody EventRequest request, HttpServletRequest http) {
        eventService.record(request, http.getHeader("User-Agent"), maskIp(clientIp(http)));
    }

    /** 운영 통계 — 기본 최근 7일. */
    @GetMapping("/api/admin/stats")
    public AdminStats stats(
            @Min(value = 1, message = "조회 일수는 1 이상이어야 합니다.")
            @Max(value = 90, message = "조회 일수는 최대 90일까지 가능합니다.")
            @RequestParam(defaultValue = "7") int days
    ) {
        return statsService.stats(days);
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /** 개인정보 보호: IPv4 마지막 옥텟 / IPv6 마지막 그룹을 0 으로 마스킹. */
    private static String maskIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return null;
        }
        int dot = ip.lastIndexOf('.');
        if (dot > 0) {
            return ip.substring(0, dot) + ".0";
        }
        int colon = ip.lastIndexOf(':');
        if (colon > 0) {
            return ip.substring(0, colon) + ":0";
        }
        return ip;
    }
}
