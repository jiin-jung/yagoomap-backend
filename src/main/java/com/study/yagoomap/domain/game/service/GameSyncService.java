package com.study.yagoomap.domain.game.service;

import com.study.yagoomap.domain.game.GameStatus;
import com.study.yagoomap.domain.game.KboTeam;
import com.study.yagoomap.domain.game.client.NaverKboClient;
import com.study.yagoomap.domain.game.client.NaverScheduleResponse;
import com.study.yagoomap.domain.game.entity.GameEntity;
import com.study.yagoomap.domain.game.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 네이버 일정을 가져와 game_schedule 테이블에 upsert(gameId 기준)한다.
 */
@Service
public class GameSyncService {

    private static final Logger log = LoggerFactory.getLogger(GameSyncService.class);

    private final NaverKboClient client;
    private final GameRepository gameRepository;

    public GameSyncService(NaverKboClient client, GameRepository gameRepository) {
        this.client = client;
        this.gameRepository = gameRepository;
    }

    /** from~to(포함) 구간을 일자별로 동기화. 반환값: 처리한 경기 수. */
    public int syncRange(LocalDate from, LocalDate to) {
        int total = 0;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            total += syncDate(d);
        }
        log.info("KBO 일정 동기화 완료 {}~{}: {}경기", from, to, total);
        return total;
    }

    @Transactional
    public int syncDate(LocalDate date) {
        List<NaverScheduleResponse.Game> games = client.fetchGamesByDate(date);
        for (NaverScheduleResponse.Game g : games) {
            upsert(g);
        }
        return games.size();
    }

    private void upsert(NaverScheduleResponse.Game g) {
        if (g.gameId() == null || g.gameDate() == null) {
            return;
        }
        GameEntity e = gameRepository.findByGameId(g.gameId()).orElseGet(GameEntity::new);
        e.setGameId(g.gameId());
        e.setGameDate(LocalDate.parse(g.gameDate()));
        e.setStartTime(parseDateTime(g.gameDateTime()));
        e.setHomeTeam(KboTeam.toKey(g.homeTeamCode(), g.homeTeamName()));
        e.setAwayTeam(KboTeam.toKey(g.awayTeamCode(), g.awayTeamName()));
        e.setHomeTeamCode(g.homeTeamCode());
        e.setAwayTeamCode(g.awayTeamCode());
        e.setStadium(g.stadium());
        e.setStatus(GameStatus.from(g.statusCode(), g.statusInfo(), g.cancel()));
        e.setStatusInfo(g.statusInfo());
        e.setHomeScore(g.homeTeamScore());
        e.setAwayScore(g.awayTeamScore());
        e.setUpdatedAt(LocalDateTime.now());
        gameRepository.save(e);
    }

    private static LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (Exception ex) {
            return null;
        }
    }
}
