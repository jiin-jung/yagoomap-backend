package com.study.yagoomap.domain.game.dto;

import com.study.yagoomap.domain.game.entity.GameEntity;

/**
 * GET /api/games 응답. 프론트는 homeTeam/awayTeam(풀네임 키)로 색·약칭을 직접 렌더한다.
 */
public record GameResponse(
        String gameId,
        String date,
        String startTime,
        String homeTeam,
        String awayTeam,
        String homeTeamCode,
        String awayTeamCode,
        String stadium,
        String status,
        String statusLabel,
        String statusInfo,
        Integer homeScore,
        Integer awayScore,
        String homeEmblem,
        String awayEmblem
) {

    private static final String EMBLEM_BASE = "https://sports-phinf.pstatic.net/team/kbo/default/";

    public static GameResponse from(GameEntity e) {
        return new GameResponse(
                e.getGameId(),
                e.getGameDate() == null ? null : e.getGameDate().toString(),
                e.getStartTime() == null ? null : e.getStartTime().toString(),
                e.getHomeTeam(),
                e.getAwayTeam(),
                e.getHomeTeamCode(),
                e.getAwayTeamCode(),
                e.getStadium(),
                e.getStatus() == null ? null : e.getStatus().name(),
                e.getStatus() == null ? null : e.getStatus().getLabel(),
                e.getStatusInfo(),
                e.getHomeScore(),
                e.getAwayScore(),
                emblem(e.getHomeTeamCode()),
                emblem(e.getAwayTeamCode())
        );
    }

    private static String emblem(String code) {
        return code == null || code.isBlank() ? null : EMBLEM_BASE + code + ".png";
    }
}
