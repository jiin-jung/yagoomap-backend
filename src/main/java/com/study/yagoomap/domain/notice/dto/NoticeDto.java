package com.study.yagoomap.domain.notice.dto;

/**
 * GET /api/notice  — 공개 응답
 * PUT /api/admin/notice — 관리자 업데이트 요청/응답 공용
 */
public record NoticeDto(String content, boolean active) {}
