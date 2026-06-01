package com.study.yagoomap.domain.presence.controller;

import com.study.yagoomap.domain.presence.dto.ActiveUsersRequest;
import com.study.yagoomap.domain.presence.dto.ActiveUsersResponse;
import com.study.yagoomap.domain.presence.service.PresenceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    /**
     * 실시간 접속자 heartbeat + 카운트 조회 (공개).
     * POST /api/active-users  body: { sessionId }  → { count }
     * 프론트가 25초 주기로 호출하며 응답의 count 를 전광판에 표시한다.
     */
    @PostMapping("/api/active-users")
    public ActiveUsersResponse active(@RequestBody(required = false) ActiveUsersRequest request) {
        String sessionId = request == null ? null : request.sessionId();
        return new ActiveUsersResponse(presenceService.touchAndCount(sessionId));
    }
}
