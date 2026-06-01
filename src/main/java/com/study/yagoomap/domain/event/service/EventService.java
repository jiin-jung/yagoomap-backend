package com.study.yagoomap.domain.event.service;

import com.study.yagoomap.domain.event.dto.EventRequest;
import com.study.yagoomap.domain.event.entity.EventLogEntity;
import com.study.yagoomap.domain.event.repository.EventLogRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 이벤트 적재(쓰기) 전담. 사용자 응답을 막지 않도록 별도 스레드풀에서 비동기 처리한다.
 * 적재 실패가 서비스 동작에 영향을 주면 안 되므로 예외는 삼키고 경고 로그만 남긴다.
 */
@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private static final String COUNTER_NAME = "yagoomap.events.total";

    private final EventLogRepository eventLogRepository;
    private final MeterRegistry meterRegistry;

    public EventService(EventLogRepository eventLogRepository, MeterRegistry meterRegistry) {
        this.eventLogRepository = eventLogRepository;
        this.meterRegistry = meterRegistry;
    }

    @Async("eventTaskExecutor")
    public void record(EventRequest request, String userAgent, String clientIp) {
        try {
            Counter.builder(COUNTER_NAME)
                    .description("프론트에서 수집한 사용자 행동 이벤트 수")
                    .tag("type", request.type().name())
                    .register(meterRegistry)
                    .increment();

            EventLogEntity entity = new EventLogEntity();
            entity.setType(request.type());
            entity.setPlaceId(request.placeId());
            entity.setKeyword(trim(request.keyword(), 255));
            entity.setTeam(trim(request.team(), 64));
            entity.setSessionId(trim(request.sessionId(), 64));
            entity.setReferrer(trim(request.referrer(), 64));
            entity.setPath(trim(request.path(), 255));
            entity.setUserAgent(trim(userAgent, 255));
            entity.setClientIp(trim(clientIp, 64));
            entity.setCreatedAt(LocalDateTime.now());
            eventLogRepository.save(entity);
        } catch (Exception exception) {
            log.warn("이벤트 적재 실패: type={}, error={}", request.type(), exception.toString());
        }
    }

    private static String trim(String value, int max) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.length() > max ? trimmed.substring(0, max) : trimmed;
    }
}
