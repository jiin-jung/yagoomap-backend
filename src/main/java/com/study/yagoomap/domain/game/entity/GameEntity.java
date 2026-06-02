package com.study.yagoomap.domain.game.entity;

import com.study.yagoomap.domain.game.GameStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * KBO 경기 일정/결과. 네이버 스포츠에서 동기화하며 gameId 기준으로 upsert 한다.
 */
@Entity
@Table(
        name = "game_schedule",
        uniqueConstraints = @UniqueConstraint(name = "uk_game_id", columnNames = "gameId"),
        indexes = {
                @Index(name = "idx_game_date", columnList = "gameDate"),
                @Index(name = "idx_game_home_team", columnList = "homeTeam"),
                @Index(name = "idx_game_away_team", columnList = "awayTeam")
        }
)
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 네이버 gameId (예: 20260602HHOB02026). upsert 키. */
    @Column(length = 32, nullable = false)
    private String gameId;

    private LocalDate gameDate;

    /** 경기 시작 시각 (gameDateTime). */
    private LocalDateTime startTime;

    /** 프론트 팀 키(풀네임). 예: "두산 베어스" */
    @Column(length = 32)
    private String homeTeam;

    @Column(length = 32)
    private String awayTeam;

    /** 네이버 팀 코드 (엠블럼/디버그용). 예: OB, HH */
    @Column(length = 8)
    private String homeTeamCode;

    @Column(length = 8)
    private String awayTeamCode;

    @Column(length = 32)
    private String stadium;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private GameStatus status;

    /** 네이버 원본 상태 라벨 (예: "경기전", "9회말", "경기취소"). LIVE 표시용. */
    @Column(length = 32)
    private String statusInfo;

    private Integer homeScore;

    private Integer awayScore;

    private LocalDateTime updatedAt;

    public GameEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public LocalDate getGameDate() {
        return gameDate;
    }

    public void setGameDate(LocalDate gameDate) {
        this.gameDate = gameDate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getHomeTeamCode() {
        return homeTeamCode;
    }

    public void setHomeTeamCode(String homeTeamCode) {
        this.homeTeamCode = homeTeamCode;
    }

    public String getAwayTeamCode() {
        return awayTeamCode;
    }

    public void setAwayTeamCode(String awayTeamCode) {
        this.awayTeamCode = awayTeamCode;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
