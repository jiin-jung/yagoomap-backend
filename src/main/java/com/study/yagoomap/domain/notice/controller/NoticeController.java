package com.study.yagoomap.domain.notice.controller;

import com.study.yagoomap.domain.notice.dto.NoticeDto;
import com.study.yagoomap.domain.notice.service.NoticeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /**
     * 공개 API — 앱 최초 로드 시 공지 배너 표시용.
     * GET /api/notice → { content, active }
     */
    @GetMapping("/api/notice")
    public NoticeDto get() {
        return noticeService.get();
    }

    /**
     * 관리자 전용 — 공지 내용·활성화 여부 갱신.
     * PUT /api/admin/notice
     * body: { content, active }
     */
    @PutMapping("/api/admin/notice")
    public NoticeDto update(@RequestBody NoticeDto dto) {
        return noticeService.update(dto);
    }
}
