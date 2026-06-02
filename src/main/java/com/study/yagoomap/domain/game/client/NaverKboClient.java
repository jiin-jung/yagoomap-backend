package com.study.yagoomap.domain.game.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * 네이버 스포츠 KBO 일정 조회. 비공식 JSON 엔드포인트라 실패에 관대하게(빈 리스트) 동작한다.
 * 범위(fromDate~toDate) 조회는 결과가 잘리는 현상이 있어 일자별 단건 조회를 기본으로 한다.
 */
@Component
public class NaverKboClient {

    private static final Logger log = LoggerFactory.getLogger(NaverKboClient.class);

    private static final String BASE_URL = "https://api-gw.sports.naver.com";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/124.0 Safari/537.36";
    private static final String REFERER = "https://m.sports.naver.com/";

    private final RestClient restClient;

    public NaverKboClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(8));
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .defaultHeader(HttpHeaders.REFERER, REFERER)
                .build();
    }

    /** 특정 날짜의 KBO 경기 목록. 실패 시 빈 리스트. */
    public List<NaverScheduleResponse.Game> fetchGamesByDate(LocalDate date) {
        String d = date.toString();
        try {
            NaverScheduleResponse res = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/schedule/games")
                            .queryParam("fields", "basic,stadium,statusInfo")
                            .queryParam("upperCategoryId", "kbaseball")
                            .queryParam("categoryId", "kbo")
                            .queryParam("fromDate", d)
                            .queryParam("toDate", d)
                            .build())
                    .retrieve()
                    .body(NaverScheduleResponse.class);
            if (res == null || res.result() == null || res.result().games() == null) {
                return List.of();
            }
            return res.result().games();
        } catch (Exception e) {
            log.warn("네이버 KBO 일정 조회 실패 date={}: {}", d, e.toString());
            return List.of();
        }
    }
}
