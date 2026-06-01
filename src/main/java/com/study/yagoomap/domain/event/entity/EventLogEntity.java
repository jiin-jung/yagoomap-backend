package com.study.yagoomap.domain.event.entity;

import com.study.yagoomap.domain.event.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 운영/통계용 이벤트 적재 테이블.
 * 개인정보(PII)는 저장하지 않는다 — IP 는 마지막 옥텟을 마스킹해서 들어온다.
 */
@Entity
@Table(name = "event_logs", indexes = {
        @Index(name = "idx_event_type", columnList = "type"),
        @Index(name = "idx_event_created_at", columnList = "createdAt"),
        @Index(name = "idx_event_place_id", columnList = "placeId"),
        @Index(name = "idx_event_session_id", columnList = "sessionId")
})
public class EventLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 32, nullable = false)
    private EventType type;

    private Long placeId;

    @Column(length = 255)
    private String keyword;

    @Column(length = 64)
    private String team;

    @Column(length = 64)
    private String sessionId;

    @Column(length = 64)
    private String referrer;

    @Column(length = 255)
    private String path;

    @Column(length = 255)
    private String userAgent;

    @Column(length = 64)
    private String clientIp;

    private LocalDateTime createdAt;

    public EventLogEntity() {
    }

    public Long getId() {
        return id;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
