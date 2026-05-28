package com.study.yagoomap.domain.place.service;

import com.study.yagoomap.domain.place.dto.ApproveReportRequest;
import com.study.yagoomap.domain.place.dto.BulkCrawlCandidateRequest;
import com.study.yagoomap.domain.place.dto.CrawlCandidate;
import com.study.yagoomap.domain.place.dto.Place;
import com.study.yagoomap.domain.place.dto.PlaceSearchCondition;
import com.study.yagoomap.domain.place.dto.ReportRequest;
import com.study.yagoomap.domain.place.dto.Review;
import com.study.yagoomap.domain.place.dto.ReviewRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:yagoomap-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PlaceServiceTest {

    @Autowired
    private PlaceService placeService;

    @Test
    void findsPlacesByKeyword() {
        var places = placeService.findPlaces(new PlaceSearchCondition("LG 트윈스", null, "강남"));

        assertThat(places)
                .extracting(Place::name)
                .containsExactly("엘지 응원집 강남");
    }

    @Test
    void findsDistrictsForTeam() {
        assertThat(placeService.findDistricts("LG 트윈스"))
                .containsExactly("강남구", "마포구", "송파구", "종로구");
    }

    @Test
    void approvesReportAsPlace() {
        var report = placeService.createReport(new ReportRequest(
                "문학 와이번스펍",
                "인천 미추홀구 매소홀로 618",
                4L,
                "SSG 랜더스",
                "SSG 경기 중계를 틀어줍니다.",
                ""
        ));

        var place = placeService.approveReport(report.id(), new ApproveReportRequest(
                null,
                null,
                37.4350,
                126.6930,
                0,
                null
        ));

        assertThat(place.name()).isEqualTo("문학 와이번스펍");
        assertThat(place.team()).isEqualTo("SSG 랜더스");
        assertThat(placeService.findReport(report.id()).status()).isEqualTo("APPROVED");
    }

    @Test
    void createsReviewAndSummarizesReviews() {
        var review = placeService.createReview(2L, new ReviewRequest("응원하기 좋은 분위기입니다.", 5));

        assertThat(placeService.findReviews(2L))
                .extracting(Review::id)
                .contains(review.id());
        assertThat(placeService.summarizeReviews(2L).summary())
                .contains("만족도");
    }

    @Test
    void createsCrawlCandidatesInBulk() {
        var candidates = placeService.createCrawlCandidates(List.of(new BulkCrawlCandidateRequest(
                "NAVER_BLOG",
                "엘지포차",
                "서울 마포구 월드컵북로 1",
                37.5,
                126.9,
                "02-123-4567",
                "술집",
                "https://map.naver.com/",
                "PENDING",
                "https://blog.naver.com/example",
                "LG 야구 포차",
                "2026-05-27T09:00:00",
                "UNCHECKED",
                List.of("LG"),
                "exact"
        )));

        assertThat(candidates)
                .extracting(CrawlCandidate::source, CrawlCandidate::sourceUrl, CrawlCandidate::sourceTeams, CrawlCandidate::matchScore)
                .containsExactly(tuple("NAVER_BLOG", "https://blog.naver.com/example", List.of("LG"), "exact"));
    }
}
