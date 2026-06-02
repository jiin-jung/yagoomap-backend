package com.study.yagoomap.domain.game.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * 네이버 스포츠 일정 API(api-gw.sports.naver.com/schedule/games) 응답 파싱용.
 * 필요한 필드만 매핑하고 나머지는 무시한다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverScheduleResponse(Result result) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(List<Game> games) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Game(
            String gameId,
            String gameDate,
            String gameDateTime,
            String stadium,
            String homeTeamCode,
            String homeTeamName,
            Integer homeTeamScore,
            String awayTeamCode,
            String awayTeamName,
            Integer awayTeamScore,
            String statusCode,
            String statusInfo,
            boolean cancel,
            boolean suspended
    ) {
    }
}
