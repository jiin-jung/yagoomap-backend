package com.study.yagoomap.domain.place.controller;

import com.study.yagoomap.domain.place.dto.ApproveCrawlCandidateRequest;
import com.study.yagoomap.domain.place.dto.ApproveReportRequest;
import com.study.yagoomap.domain.place.dto.CrawlCandidate;
import com.study.yagoomap.domain.place.dto.CrawlCandidateRequest;
import com.study.yagoomap.domain.place.dto.Dashboard;
import com.study.yagoomap.domain.place.dto.KakaoCollectRequest;
import com.study.yagoomap.domain.place.dto.KakaoSearchRequest;
import com.study.yagoomap.domain.place.dto.Place;
import com.study.yagoomap.domain.place.dto.PlaceRequest;
import com.study.yagoomap.domain.place.dto.PlaceSearchCondition;
import com.study.yagoomap.domain.place.dto.RejectReportRequest;
import com.study.yagoomap.domain.place.dto.Report;
import com.study.yagoomap.domain.place.dto.ReportRequest;
import com.study.yagoomap.domain.place.dto.Review;
import com.study.yagoomap.domain.place.dto.ReviewRequest;
import com.study.yagoomap.domain.place.dto.ReviewSummary;
import com.study.yagoomap.domain.place.service.KakaoLocalClient;
import com.study.yagoomap.domain.place.service.NaverImageClient;
import com.study.yagoomap.domain.place.service.PlaceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@Validated
public class PlaceController {

    private final PlaceService placeService;
    private final NaverImageClient naverImageClient;

    public PlaceController(PlaceService placeService, NaverImageClient naverImageClient) {
        this.placeService = placeService;
        this.naverImageClient = naverImageClient;
    }

    @GetMapping("/api/places")
    public List<Place> places(
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String keyword
    ) {
        return placeService.findPlaces(new PlaceSearchCondition(team, district, keyword));
    }

    @GetMapping("/api/teams/{team}/places")
    public List<Place> placesByTeam(@PathVariable String team) {
        return placeService.findPlaces(new PlaceSearchCondition(team, null, null));
    }

    @GetMapping("/api/places/{placeId}")
    public Place place(@PathVariable long placeId) {
        return placeService.findPlace(placeId);
    }

    @PostMapping("/api/places")
    public Place createPlace(@Valid @RequestBody PlaceRequest request) {
        return placeService.createPlace(request);
    }

    @PatchMapping("/api/places/{placeId}")
    public Place updatePlace(@PathVariable long placeId, @Valid @RequestBody PlaceRequest request) {
        return placeService.updatePlace(placeId, request);
    }

    @DeleteMapping("/api/places/{placeId}")
    public void deletePlace(@PathVariable long placeId) {
        placeService.deletePlace(placeId);
    }

    @GetMapping("/api/place-filters")
    public PlaceFilters filters(@RequestParam(required = false) String team) {
        return new PlaceFilters(placeService.findTeams(), placeService.findDistricts(team));
    }

    @GetMapping("/api/images")
    public NaverImageClient.ImageSearchResponse images(
            @NotBlank(message = "검색어는 필수입니다.") @RequestParam String query
    ) {
        return naverImageClient.search(query);
    }

    @PostMapping("/api/reports")
    public Report createReport(@Valid @RequestBody ReportRequest request) {
        return placeService.createReport(request);
    }

    @GetMapping("/api/admin/reports")
    public List<Report> reports(@RequestParam(required = false) String status) {
        return placeService.findReports(status);
    }

    @GetMapping("/api/admin/reports/{reportId}")
    public Report report(@PathVariable long reportId) {
        return placeService.findReport(reportId);
    }

    @PostMapping("/api/admin/reports/{reportId}/approve")
    public Place approveReport(
            @PathVariable long reportId,
            @Valid @RequestBody ApproveReportRequest request
    ) {
        return placeService.approveReport(reportId, request);
    }

    @PostMapping("/api/admin/reports/{reportId}/reject")
    public Report rejectReport(
            @PathVariable long reportId,
            @Valid @RequestBody RejectReportRequest request
    ) {
        return placeService.rejectReport(reportId, request);
    }

    @PostMapping("/api/places/{placeId}/reviews")
    public Review createReview(
            @PathVariable long placeId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return placeService.createReview(placeId, request);
    }

    @GetMapping("/api/places/{placeId}/reviews")
    public List<Review> reviews(@PathVariable long placeId) {
        return placeService.findReviews(placeId);
    }

    @DeleteMapping("/api/places/{placeId}/reviews/{reviewId}")
    public void deleteReview(@PathVariable long placeId, @PathVariable long reviewId) {
        placeService.deleteReview(placeId, reviewId);
    }

    @GetMapping("/api/admin/places")
    public List<Place> adminPlaces() {
        return placeService.findAllPlacesForAdmin();
    }

    @GetMapping("/api/admin/dashboard")
    public Dashboard dashboard() {
        return placeService.dashboard();
    }

    @GetMapping("/api/places/{placeId}/reviews/summary")
    public ReviewSummary reviewSummary(@PathVariable long placeId) {
        return placeService.summarizeReviews(placeId);
    }

    @PostMapping("/api/admin/crawl-candidates")
    public CrawlCandidate createCrawlCandidate(@Valid @RequestBody CrawlCandidateRequest request) {
        return placeService.createCrawlCandidate(request);
    }

    @GetMapping("/api/admin/crawl-candidates")
    public List<CrawlCandidate> crawlCandidates(@RequestParam(required = false) String status) {
        return placeService.findCrawlCandidates(status);
    }

    @PostMapping("/api/admin/crawl-candidates/{candidateId}/approve")
    public Place approveCrawlCandidate(
            @PathVariable long candidateId,
            @Valid @RequestBody ApproveCrawlCandidateRequest request
    ) {
        return placeService.approveCrawlCandidate(candidateId, request);
    }

    @PostMapping("/api/admin/crawl-candidates/{candidateId}/reject")
    public CrawlCandidate rejectCrawlCandidate(
            @PathVariable long candidateId,
            @Valid @RequestBody RejectReportRequest request
    ) {
        return placeService.rejectCrawlCandidate(candidateId, request);
    }

    @GetMapping("/api/admin/kakao/places")
    public KakaoLocalClient.KakaoPlaceSearchResponse kakaoPlaces(
            @NotBlank(message = "검색어는 필수입니다.") @RequestParam String query,
            @RequestParam(required = false, defaultValue = "FD6") String categoryGroupCode,
            @RequestParam(required = false, defaultValue = "0") double longitude,
            @RequestParam(required = false, defaultValue = "0") double latitude,
            @Min(value = 0, message = "반경은 0 이상이어야 합니다.")
            @Max(value = 20000, message = "반경은 최대 20000m까지 가능합니다.")
            @RequestParam(required = false, defaultValue = "0") int radius,
            @RequestParam(required = false) String rect,
            @Min(value = 1, message = "페이지는 1 이상이어야 합니다.")
            @Max(value = 45, message = "페이지는 45 이하이어야 합니다.")
            @RequestParam(required = false, defaultValue = "1") int page,
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
            @Max(value = 15, message = "페이지 크기는 15 이하이어야 합니다.")
            @RequestParam(required = false, defaultValue = "15") int size,
            @RequestParam(required = false, defaultValue = "accuracy") String sort
    ) {
        return placeService.searchKakaoPlaces(new KakaoSearchRequest(query, categoryGroupCode, longitude, latitude, radius, rect, page, size, sort));
    }

    @PostMapping("/api/admin/kakao/places/collect")
    public List<CrawlCandidate> collectKakaoPlaces(@Valid @RequestBody KakaoCollectRequest request) {
        return placeService.collectKakaoPlaces(request);
    }

    public record PlaceFilters(List<String> teams, List<String> districts) {
    }
}
