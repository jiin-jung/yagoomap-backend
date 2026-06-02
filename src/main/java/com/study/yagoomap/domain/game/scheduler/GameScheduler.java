package com.study.yagoomap.domain.game.scheduler;

import com.study.yagoomap.domain.game.service.GameSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * KBO 일정 동기화 스케줄.
 * - 매일 새벽: 향후 일정 풀 동기화
 * - 저녁 경기 시간대: 오늘 경기 라이브(스코어/상태) 갱신
 */
@Component
public class GameScheduler {

    private static final Logger log = LoggerFactory.getLogger(GameScheduler.class);

    private static final int FORWARD_DAYS = 30;

    private final GameSyncService gameSyncService;

    public GameScheduler(GameSyncService gameSyncService) {
        this.gameSyncService = gameSyncService;
    }

    /** 매일 04:10(KST) — 어제부터 향후 30일까지 풀 동기화. */
    @Scheduled(cron = "0 10 4 * * *", zone = "Asia/Seoul")
    public void dailyFullSync() {
        LocalDate today = LocalDate.now();
        gameSyncService.syncRange(today.minusDays(1), today.plusDays(FORWARD_DAYS));
    }

    /** 17~23시 10분마다(KST) — 오늘 경기 스코어/상태 갱신. */
    @Scheduled(cron = "0 0/10 17-23 * * *", zone = "Asia/Seoul")
    public void liveRefresh() {
        int n = gameSyncService.syncDate(LocalDate.now());
        log.debug("라이브 갱신: 오늘 {}경기", n);
    }
}
