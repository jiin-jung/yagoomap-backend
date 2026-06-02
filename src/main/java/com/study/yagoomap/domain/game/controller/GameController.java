package com.study.yagoomap.domain.game.controller;

import com.study.yagoomap.domain.game.dto.GameResponse;
import com.study.yagoomap.domain.game.service.GameService;
import com.study.yagoomap.domain.game.service.GameSyncService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
public class GameController {

    private final GameService gameService;
    private final GameSyncService gameSyncService;

    public GameController(GameService gameService, GameSyncService gameSyncService) {
        this.gameService = gameService;
        this.gameSyncService = gameSyncService;
    }

    /** 특정 날짜(미지정 시 오늘)의 KBO 경기. date=YYYY-MM-DD */
    @GetMapping("/api/games")
    public List<GameResponse> games(@RequestParam(required = false) String date) {
        return gameService.gamesByDate(parseDateOrToday(date));
    }

    /** 오늘 경기. */
    @GetMapping("/api/games/today")
    public List<GameResponse> today() {
        return gameService.todayGames();
    }

    /** 수동 동기화(운영용). 어제~+days(기본 30) 구간. */
    @PostMapping("/api/admin/games/sync")
    public Map<String, Object> sync(@RequestParam(required = false) Integer days) {
        int forward = days == null ? 30 : Math.max(0, Math.min(days, 120));
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(1);
        LocalDate to = today.plusDays(forward);
        int synced = gameSyncService.syncRange(from, to);
        return Map.of(
                "synced", synced,
                "from", from.toString(),
                "to", to.toString()
        );
    }

    private static LocalDate parseDateOrToday(String date) {
        if (date == null || date.isBlank()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(date.trim());
        } catch (DateTimeParseException e) {
            return LocalDate.now();
        }
    }
}
