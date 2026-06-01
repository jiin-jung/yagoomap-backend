package com.study.yagoomap.domain.notice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 앱 공지사항 — 항상 id=1 인 단일 행(singleton) 으로 관리한다.
 * ddl-auto:update 로 자동 생성되므로 별도 마이그레이션 불필요.
 */
@Entity
@Table(name = "notices")
public class NoticeEntity {

    /** 공지는 최대 1건 — id 는 항상 1 */
    @Id
    private Long id = 1L;

    @Column(length = 500)
    private String content;

    @Column(nullable = false)
    private boolean active = false;

    private LocalDateTime updatedAt;

    public NoticeEntity() {}

    public Long getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
