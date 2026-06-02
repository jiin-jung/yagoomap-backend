package com.study.yagoomap.domain.game.scheduler;

import com.study.yagoomap.domain.game.service.GameSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 앱 기동 직후 1회 초기 동기화. 네이버 응답 지연이 기동을 막지 않도록 데몬 스레드에서 수행한다.
 */
@Component
public class GameDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(GameDataInitializer.class);

    private final GameSyncService gameSyncService;

    public GameDataInitializer(GameSyncService gameSyncService) {
        this.gameSyncService = gameSyncService;
    }

    @Override
    public void run(ApplicationArguments args) {
        Thread t = new Thread(() -> {
            try {
                LocalDate today = LocalDate.now();
                gameSyncService.syncRange(today.minusDays(1), today.plusDays(14));
            } catch (Exception e) {
                log.warn("초기 KBO 일정 동기화 실패: {}", e.toString());
            }
        }, "game-init");
        t.setDaemon(true);
        t.start();
    }
}
