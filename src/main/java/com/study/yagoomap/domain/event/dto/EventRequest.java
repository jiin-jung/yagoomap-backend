package com.study.yagoomap.domain.event.dto;

import com.study.yagoomap.domain.event.EventType;
import jakarta.validation.constraints.NotNull;

/**
 * POST /api/events 요청 본문. 프론트가 fire-and-forget 으로 전송한다.
 * type 외에는 전부 선택값 — 이벤트 종류에 따라 필요한 필드만 채워 보낸다.
 */
public record EventRequest(
        @NotNull(message = "이벤트 타입은 필수입니다.") EventType type,
        Long placeId,
        String keyword,
        String team,
        String sessionId,
        String referrer,
        String path
) {
}
