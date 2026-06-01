package com.study.yagoomap.domain.notice.service;

import com.study.yagoomap.domain.notice.dto.NoticeDto;
import com.study.yagoomap.domain.notice.entity.NoticeEntity;
import com.study.yagoomap.domain.notice.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    /** 현재 공지 조회 — 행이 없으면 빈 응답 반환 */
    @Transactional(readOnly = true)
    public NoticeDto get() {
        return noticeRepository.findById(1L)
                .map(n -> new NoticeDto(n.getContent(), n.isActive()))
                .orElse(new NoticeDto(null, false));
    }

    /** 공지 저장/갱신 — id=1 단일 행 upsert */
    @Transactional
    public NoticeDto update(NoticeDto dto) {
        NoticeEntity notice = noticeRepository.findById(1L)
                .orElse(new NoticeEntity());
        notice.setContent(dto.content() != null ? dto.content().strip() : null);
        notice.setActive(dto.active());
        notice.setUpdatedAt(LocalDateTime.now());
        noticeRepository.save(notice);
        return new NoticeDto(notice.getContent(), notice.isActive());
    }
}
