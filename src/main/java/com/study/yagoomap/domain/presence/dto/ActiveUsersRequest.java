package com.study.yagoomap.domain.presence.dto;

/** POST /api/active-users 요청 — 익명 세션 ID(개인정보 아님) */
public record ActiveUsersRequest(String sessionId) {}
