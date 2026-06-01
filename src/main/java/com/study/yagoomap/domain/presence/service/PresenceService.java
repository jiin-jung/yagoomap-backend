package com.study.yagoomap.domain.presence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

/**
 * 실시간 접속자(presence) 집계.
 *
 * Redis Sorted Set 에 sessionId(member) / 마지막 활동시각(score, epoch ms) 을 기록한다.
 *  - touch: 세션 갱신 + 윈도우(75s) 밖 항목 정리 + 현재 카운트 반환
 *  - 프론트가 25초마다 heartbeat 를 보내므로 75초 윈도우면 누락 없이 유지된다.
 *
 * Redis 장애 시에도 서비스에 영향 없도록 예외는 삼키고 0 을 반환한다.
 */
@Service
public class PresenceService {

    private static final Logger log = LoggerFactory.getLogger(PresenceService.class);

    private static final String KEY = "active_users";
    private static final long WINDOW_MS = 75_000L;

    private final StringRedisTemplate redis;

    public PresenceService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /** 세션 활동 갱신 후 현재 활성 세션 수를 반환한다. */
    public long touchAndCount(String sessionId) {
        try {
            long now = System.currentTimeMillis();
            ZSetOperations<String, String> zset = redis.opsForZSet();

            if (sessionId != null && !sessionId.isBlank()) {
                String member = sessionId.length() > 64 ? sessionId.substring(0, 64) : sessionId;
                zset.add(KEY, member, now);
            }

            // 윈도우 밖(오래된) 세션 제거
            zset.removeRangeByScore(KEY, 0, now - WINDOW_MS);

            Long count = zset.size(KEY);
            return count == null ? 0 : count;
        } catch (Exception exception) {
            log.warn("실시간 접속자 집계 실패: {}", exception.toString());
            return 0;
        }
    }
}
