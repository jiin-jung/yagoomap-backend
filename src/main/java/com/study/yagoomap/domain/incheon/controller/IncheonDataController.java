package com.study.yagoomap.domain.incheon.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 인천 공공데이터 프록시 (인천 공공데이터·AI 활용 창업경진대회 출품 기능).
 * 프론트의 "가는 길 + AI 직관 도우미" 섹션이 호출한다.
 *
 * - GET /api/busArrival?bstopId= : 인천광역시_도착정보 조회 (data.go.kr, XML 릴레이)
 * - GET /api/weather?nx=&ny=     : 기상청_단기예보 조회 (data.go.kr, JSON 릴레이)
 *
 * 인증키 (data.go.kr 일반 인증키 하나로 두 서비스 모두 사용):
 * - 로컬: .env의 INCHEON_BUS_API_KEY
 * - 운영: deploy.yml이 생성하는 application-prod.yml의 incheon.api-key (GitHub secret)
 */
@RestController
public class IncheonDataController {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    /** 기상청 단기예보 발표시각 (내림차순) — 발표 후 약 10분 뒤 제공되어 15분 여유를 둔다. */
    private static final int[] BASE_HOURS = {23, 20, 17, 14, 11, 8, 5, 2};

    private final RestClient restClient = RestClient.create();
    private final String apiKey;

    public IncheonDataController(@Value("${incheon.api-key:${INCHEON_BUS_API_KEY:}}") String apiKey) {
        this.apiKey = apiKey;
    }

    /** 인천 버스 도착정보 — 정류소ID 기준 전체 노선 도착예정 (XML 그대로 릴레이). */
    @GetMapping(value = "/api/busArrival", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> busArrival(@RequestParam String bstopId) {
        if (apiKey.isBlank()) return keyMissing();

        String url = "https://apis.data.go.kr/6280000/busArrivalService/getAllRouteBusArrivalList"
                + "?serviceKey=" + apiKey
                + "&bstopId=" + bstopId
                + "&numOfRows=5&pageNo=1";
        return relay(url);
    }

    /** 기상청 단기예보 — 격자좌표(nx, ny) 기준 최신 발표분 (JSON 그대로 릴레이). */
    @GetMapping(value = "/api/weather", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> weather(@RequestParam String nx, @RequestParam String ny) {
        if (apiKey.isBlank()) return keyMissing();

        ZonedDateTime now = ZonedDateTime.now(KST);
        Integer baseHour = null;
        for (int h : BASE_HOURS) {
            if (now.getHour() > h || (now.getHour() == h && now.getMinute() >= 15)) {
                baseHour = h;
                break;
            }
        }
        ZonedDateTime baseDay = now;
        if (baseHour == null) { // 00:00 ~ 02:14 → 전날 23시 발표분
            baseHour = 23;
            baseDay = now.minusDays(1);
        }

        String url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"
                + "?serviceKey=" + apiKey
                + "&dataType=JSON&numOfRows=400&pageNo=1"
                + "&base_date=" + baseDay.format(DateTimeFormatter.BASIC_ISO_DATE)
                + "&base_time=" + String.format("%02d00", baseHour)
                + "&nx=" + nx + "&ny=" + ny;
        return relay(url);
    }

    /** data.go.kr 응답을 그대로 전달. serviceKey 이중 인코딩 방지를 위해 URI.create 사용. */
    private ResponseEntity<?> relay(String url) {
        try {
            String body = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(String.class);
            return ResponseEntity.ok(body);
        } catch (RestClientException e) {
            return ResponseEntity.status(502)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "공공데이터 API 호출 실패", "detail", e.getMessage()));
        }
    }

    private ResponseEntity<?> keyMissing() {
        return ResponseEntity.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("error", "INCHEON_BUS_API_KEY 미설정"));
    }
}
