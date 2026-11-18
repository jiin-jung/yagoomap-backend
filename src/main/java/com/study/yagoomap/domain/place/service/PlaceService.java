package com.study.yagoomap.domain.place.service;

import com.study.yagoomap.domain.place.dto.ApproveCrawlCandidateRequest;
import com.study.yagoomap.domain.place.dto.ApproveReportRequest;
import com.study.yagoomap.domain.place.dto.BulkCrawlCandidateRequest;
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
import com.study.yagoomap.domain.place.entity.CrawlCandidateEntity;
import com.study.yagoomap.domain.place.entity.PlaceEntity;
import com.study.yagoomap.domain.place.entity.ReportEntity;
import com.study.yagoomap.domain.place.entity.ReviewEntity;
import com.study.yagoomap.domain.place.repository.CrawlCandidateRepository;
import com.study.yagoomap.domain.place.repository.PlaceRepository;
import com.study.yagoomap.domain.place.repository.ReportRepository;
import com.study.yagoomap.domain.place.repository.ReviewRepository;
import com.study.yagoomap.global.error.ApiException;
import com.study.yagoomap.global.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class PlaceService {

    private static final String ACTIVE = "ACTIVE";
    private static final String INACTIVE = "INACTIVE";
    private static final String PENDING = "PENDING";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";
    private static final String DUPLICATE = "DUPLICATE";
    private final PlaceRepository placeRepository;
    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    private final CrawlCandidateRepository crawlCandidateRepository;
    private final KakaoLocalClient kakaoLocalClient;

    public PlaceService(
            PlaceRepository placeRepository,
            ReportRepository reportRepository,
            ReviewRepository reviewRepository,
            CrawlCandidateRepository crawlCandidateRepository,
            KakaoLocalClient kakaoLocalClient
    ) {
        this.placeRepository = placeRepository;
        this.reportRepository = reportRepository;
        this.reviewRepository = reviewRepository;
        this.crawlCandidateRepository = crawlCandidateRepository;
        this.kakaoLocalClient = kakaoLocalClient;
    }

    @Transactional(readOnly = true)
    public List<Place> findPlaces(PlaceSearchCondition condition) {
        return placeRepository.findByStatus(ACTIVE).stream()
                .filter(place -> matches(condition.team(), place.getTeam()))
                .filter(place -> matches(condition.district(), place.getDistrict()))
                .filter(place -> containsKeyword(condition.keyword(), place))
                .sorted(Comparator.comparing(PlaceEntity::getDistrict).thenComparing(PlaceEntity::getName))
                .map(this::toPlace)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Place> findAllPlacesForAdmin() {
        return placeRepository.findAll().stream()
                .sorted(Comparator.comparing(PlaceEntity::getId))
                .map(this::toPlace)
                .toList();
    }

    @Transactional(readOnly = true)
    public Place findPlace(long placeId) {
        PlaceEntity place = findActivePlace(placeId);
        return toPlace(place);
    }

    public Place createPlace(PlaceRequest request) {
        Optional<String> duplicateReason = findDuplicateReason(duplicateCandidates(), request.kakaoPlaceId(), request.name(), request.address(), request.phone(), request.latitude(), request.longitude(), null);
        if (duplicateReason.isPresent()) {
            throw new ApiException(ErrorCode.PLACE_DUPLICATED, duplicateReason.get());
        }

        PlaceEntity place = new PlaceEntity();
        applyPlaceRequest(place, request);
        place.setStatus(valueOrDefault(request.status(), ACTIVE));
        place.setRating(0.0);
        place.setReviewCount(0);
        place.setDistanceMeters(0);
        place.setCreatedAt(now());
        place.setUpdatedAt(now());
        return toPlace(placeRepository.save(place));
    }

    public Place updatePlace(long placeId, PlaceRequest request) {
        PlaceEntity place = findAnyPlace(placeId);
        Optional<String> duplicateReason = findDuplicateReason(
                duplicateCandidates(),
                valueOrDefault(request.kakaoPlaceId(), place.getKakaoPlaceId()),
                valueOrDefault(request.name(), place.getName()),
                valueOrDefault(request.address(), place.getAddress()),
                valueOrDefault(request.phone(), place.getPhone()),
                request.latitude() == 0 ? place.getLatitude() : request.latitude(),
                request.longitude() == 0 ? place.getLongitude() : request.longitude(),
                place.getId()
        );
        if (duplicateReason.isPresent()) {
            throw new ApiException(ErrorCode.PLACE_DUPLICATED, duplicateReason.get());
        }

        applyPlaceRequest(place, request);
        place.setUpdatedAt(now());
        return toPlace(place);
    }

    public void deletePlace(long placeId) {
        PlaceEntity place = findAnyPlace(placeId);
        place.setStatus(INACTIVE);
        place.setUpdatedAt(now());
    }

    @Transactional(readOnly = true)
    public List<String> findTeams() {
        return placeRepository.findByStatus(ACTIVE).stream()
                .map(PlaceEntity::getTeam)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> findDistricts(String team) {
        return placeRepository.findByStatus(ACTIVE).stream()
                .filter(place -> matches(team, place.getTeam()))
                .map(PlaceEntity::getDistrict)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    public Report createReport(ReportRequest request) {
        return toReport(reportRepository.save(report(
                request.placeName(),
                request.address(),
                request.teamId(),
                request.team(),
                Optional.ofNullable(request.content()).orElse(""),
                Optional.ofNullable(request.referenceLink()).orElse("")
        )));
    }

    @Transactional(readOnly = true)
    public List<Report> findReports(String status) {
        List<ReportEntity> reports = status == null || status.isBlank()
                ? reportRepository.findAll()
                : reportRepository.findByStatus(status);
        return reports.stream()
                .sorted(Comparator.comparing(ReportEntity::getCreatedAt).reversed())
                .map(this::toReport)
                .toList();
    }

    @Transactional(readOnly = true)
    public Report findReport(long reportId) {
        return toReport(findReportEntity(reportId));
    }

    public Place approveReport(long reportId, ApproveReportRequest request) {
        ReportEntity report = findReportEntity(reportId);
        report.setStatus(APPROVED);
        report.setRejectReason(null);

        return createPlace(new PlaceRequest(
                null,
                valueOrDefault(request.name(), report.getPlaceName()),
                valueOrDefault(request.address(), report.getAddress()),
                request.latitude(),
                request.longitude(),
                request.teamId() == 0 ? report.getTeamId() : request.teamId(),
                valueOrDefault(request.team(), report.getTeam()),
                "술집",
                null,
                "",
                "",
                "",
                report.getContent(),
                List.of("제보 승인"),
                ACTIVE
        ));
    }

    public Report rejectReport(long reportId, RejectReportRequest request) {
        ReportEntity report = findReportEntity(reportId);
        report.setStatus(REJECTED);
        report.setRejectReason(request.reason());
        return toReport(report);
    }

    public Review createReview(long placeId, ReviewRequest request) {
        findActivePlace(placeId);
        ReviewEntity review = new ReviewEntity();
        review.setPlaceId(placeId);
        review.setContent(request.content());
        review.setRating(request.rating());
        review.setCreatedAt(now());
        review.setActive(true);
        ReviewEntity saved = reviewRepository.save(review);
        refreshPlaceRating(placeId);
        return toReview(saved);
    }

    @Transactional(readOnly = true)
    public List<Review> findReviews(long placeId) {
        findActivePlace(placeId);
        return reviewRepository.findByPlaceIdAndActive(placeId, true).stream()
                .sorted(Comparator.comparing(ReviewEntity::getCreatedAt).reversed())
                .map(this::toReview)
                .toList();
    }

    public void deleteReview(long placeId, long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .filter(item -> item.getPlaceId() == placeId)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND));
        review.setActive(false);
        refreshPlaceRating(placeId);
    }

    @Transactional(readOnly = true)
    public ReviewSummary summarizeReviews(long placeId) {
        List<Review> activeReviews = findReviews(placeId);
        if (activeReviews.isEmpty()) {
            return new ReviewSummary(placeId, 0.0, 0, "아직 요약할 리뷰가 없습니다.");
        }
        double average = activeReviews.stream().mapToInt(Review::rating).average().orElse(0.0);
        String summary = average >= 4.5
                ? "응원 분위기와 관람 환경에 대한 만족도가 높습니다."
                : "방문 전 좌석, 화면, 예약 가능 여부를 확인하는 것이 좋습니다.";
        return new ReviewSummary(placeId, Math.round(average * 10) / 10.0, activeReviews.size(), summary);
    }

    @Transactional(readOnly = true)
    public Dashboard dashboard() {
        long activePlaces = placeRepository.countByStatus(ACTIVE);
        long pendingReports = reportRepository.countByStatus(PENDING);
        long activeReviews = reviewRepository.countByActive(true);
        return new Dashboard(activePlaces, reportRepository.count(), pendingReports, activeReviews, placeRepository.findTop5ByOrderByIdAsc().stream().map(this::toPlace).toList());
    }

    public CrawlCandidate createCrawlCandidate(CrawlCandidateRequest request) {
        CrawlCandidateEntity candidate = new CrawlCandidateEntity();
        candidate.setSource("MANUAL");
        candidate.setKeyword(request.keyword());
        candidate.setName(request.name());
        candidate.setAddress(request.address());
        candidate.setPhone(request.phone());
        candidate.setMapLink(request.mapLink());
        candidate.setCollectedAt(now());
        markDuplicateOrPending(candidate, duplicateCandidates());
        return toCrawlCandidate(crawlCandidateRepository.save(candidate));
    }

    public List<CrawlCandidate> createCrawlCandidates(List<BulkCrawlCandidateRequest> requests) {
        List<CrawlCandidateEntity> candidates = requests.stream()
                .map(this::toCrawlCandidateEntity)
                .toList();
        return crawlCandidateRepository.saveAll(candidates).stream()
                .map(this::toCrawlCandidate)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CrawlCandidate> findCrawlCandidates(String status) {
        List<CrawlCandidateEntity> candidates = status == null || status.isBlank()
                ? crawlCandidateRepository.findAll()
                : crawlCandidateRepository.findByStatus(status);
        return candidates.stream()
                .sorted(Comparator.comparing(CrawlCandidateEntity::getCollectedAt).reversed())
                .map(this::toCrawlCandidate)
                .toList();
    }

    public Place approveCrawlCandidate(long candidateId, ApproveCrawlCandidateRequest request) {
        CrawlCandidateEntity candidate = findCrawlCandidateEntity(candidateId);
        if (DUPLICATE.equals(candidate.getStatus())) {
            throw new ApiException(ErrorCode.CRAWL_CANDIDATE_DUPLICATED, valueOrDefault(candidate.getDuplicateReason(), ErrorCode.CRAWL_CANDIDATE_DUPLICATED.message()));
        }

        candidate.setStatus(APPROVED);

        return createPlace(new PlaceRequest(
                candidate.getSourceId(),
                valueOrDefault(request.name(), candidate.getName()),
                valueOrDefault(request.address(), valueOrDefault(candidate.getRoadAddress(), candidate.getAddress())),
                request.latitude() == 0 ? candidate.getLatitude() : request.latitude(),
                request.longitude() == 0 ? candidate.getLongitude() : request.longitude(),
                request.teamId(),
                request.team(),
                lastCategory(candidate.getCategoryName()),
                candidate.getCategoryName(),
                candidate.getCategoryGroupCode(),
                candidate.getPhone(),
                candidate.getMapLink(),
                request.note(),
                request.tags() == null ? List.of("검수 승인") : request.tags(),
                ACTIVE
        ));
    }

    public CrawlCandidate rejectCrawlCandidate(long candidateId, RejectReportRequest request) {
        CrawlCandidateEntity candidate = findCrawlCandidateEntity(candidateId);
        candidate.setStatus(REJECTED);
        candidate.setDuplicateReason(request.reason());
        return toCrawlCandidate(candidate);
    }

    @Transactional(readOnly = true)
    public KakaoLocalClient.KakaoPlaceSearchResponse searchKakaoPlaces(KakaoSearchRequest request) {
        return kakaoLocalClient.searchKeyword(new KakaoLocalClient.KakaoPlaceSearchRequest(
                request.query(), valueOrDefault(request.categoryGroupCode(), "FD6"),
                request.longitude(), request.latitude(), request.radius(), request.rect(),
                request.page(), request.size(), request.sort()
        ));
    }

    public List<CrawlCandidate> collectKakaoPlaces(KakaoCollectRequest request) {
        KakaoLocalClient.KakaoPlaceSearchResponse response = kakaoLocalClient.searchKeyword(new KakaoLocalClient.KakaoPlaceSearchRequest(
                request.query(), valueOrDefault(request.categoryGroupCode(), "FD6"),
                request.longitude(), request.latitude(), request.radius(), request.rect(),
                request.page(), request.size(), request.sort()
        ));

        List<PlaceEntity> places = duplicateCandidates();
        return Optional.ofNullable(response.documents()).orElse(List.of()).stream()
                .map(document -> saveKakaoCandidate(request.query(), document, places))
                .toList();
    }

    private CrawlCandidate saveKakaoCandidate(String keyword, KakaoLocalClient.KakaoPlaceDocument document, List<PlaceEntity> places) {
        Optional<CrawlCandidateEntity> existing = crawlCandidateRepository.findBySourceAndSourceId("KAKAO", document.id());
        if (existing.isPresent()) {
            return toCrawlCandidate(existing.get());
        }

        CrawlCandidateEntity candidate = new CrawlCandidateEntity();
        candidate.setSource("KAKAO");
        candidate.setSourceId(document.id());
        candidate.setKeyword(keyword);
        candidate.setName(document.place_name());
        candidate.setAddress(document.address_name());
        candidate.setRoadAddress(document.road_address_name());
        candidate.setPhone(document.phone());
        candidate.setMapLink(document.place_url());
        candidate.setCategoryName(document.category_name());
        candidate.setCategoryGroupCode(document.category_group_code());
        candidate.setLongitude(toDouble(document.x()));
        candidate.setLatitude(toDouble(document.y()));
        candidate.setDistanceMeters(toInt(document.distance()));
        candidate.setCollectedAt(now());
        markDuplicateOrPending(candidate, places);
        return toCrawlCandidate(crawlCandidateRepository.save(candidate));
    }

    private CrawlCandidateEntity toCrawlCandidateEntity(BulkCrawlCandidateRequest request) {
        CrawlCandidateEntity candidate = new CrawlCandidateEntity();
        candidate.setSource(request.source());
        candidate.setKeyword(request.keyword());
        candidate.setName(request.name());
        candidate.setAddress(request.address());
        candidate.setPhone(request.phone());
        candidate.setMapLink(request.mapLink());
        candidate.setCategoryName(request.categoryName());
        candidate.setLatitude(request.latitude());
        candidate.setLongitude(request.longitude());
        candidate.setCollectedAt(parseCollectedAt(request.collectedAt()));
        candidate.setStatus(valueOrDefault(request.status(), PENDING));
        candidate.setSourceUrl(request.sourceUrl());
        candidate.setDuplicateCheckResult(valueOrDefault(request.duplicateCheckResult(), "UNCHECKED"));
        candidate.setSourceTeams(request.sourceTeams());
        candidate.setMatchScore(request.matchScore());
        return candidate;
    }

    private void markDuplicateOrPending(CrawlCandidateEntity candidate, List<PlaceEntity> places) {
        Optional<String> duplicateReason = findDuplicateReason(
                places,
                candidate.getSourceId(),
                candidate.getName(),
                valueOrDefault(candidate.getRoadAddress(), candidate.getAddress()),
                candidate.getPhone(),
                candidate.getLatitude(),
                candidate.getLongitude(),
                null
        );
        candidate.setStatus(duplicateReason.isPresent() ? DUPLICATE : PENDING);
        candidate.setDuplicateReason(duplicateReason.orElse(null));
    }

    /** 중복 검사 대상(비활성 제외). 여러 건을 검사할 땐 한 번만 불러서 재사용한다. */
    List<PlaceEntity> duplicateCandidates() {
        return placeRepository.findAll().stream()
                .filter(place -> !INACTIVE.equals(place.getStatus()))
                .toList();
    }

    private Optional<String> findDuplicateReason(List<PlaceEntity> places, String kakaoPlaceId, String name, String address, String phone, double latitude, double longitude, Long exceptPlaceId) {
        return places.stream()
                .filter(place -> exceptPlaceId == null || !exceptPlaceId.equals(place.getId()))
                .map(place -> duplicateReason(place, kakaoPlaceId, name, address, phone, latitude, longitude))
                .flatMap(Optional::stream)
                .findFirst();
    }

    boolean isDuplicate(List<PlaceEntity> places, String name, String address, String phone, double latitude, double longitude) {
        return findDuplicateReason(places, null, name, address, phone, latitude, longitude, null).isPresent();
    }

    private Optional<String> duplicateReason(PlaceEntity place, String kakaoPlaceId, String name, String address, String phone, double latitude, double longitude) {
        if (!isBlank(kakaoPlaceId) && kakaoPlaceId.equals(place.getKakaoPlaceId())) {
            return Optional.of("카카오 장소 ID가 기존 장소와 같습니다.");
        }
        if (!isBlank(name) && !isBlank(address) && normalize(name).equals(normalize(place.getName())) && normalize(address).equals(normalize(place.getAddress()))) {
            return Optional.of("가게명과 주소가 기존 장소와 같습니다.");
        }
        if (!isBlank(phone) && phone.equals(place.getPhone())) {
            return Optional.of("전화번호가 기존 장소와 같습니다.");
        }
        if (latitude != 0 && longitude != 0 && distanceMeters(latitude, longitude, place.getLatitude(), place.getLongitude()) <= 30
                && !isBlank(name) && normalize(name).equals(normalize(place.getName()))) {
            return Optional.of("같은 이름의 장소가 30m 안에 있습니다.");
        }
        return Optional.empty();
    }

    private void applyPlaceRequest(PlaceEntity place, PlaceRequest request) {
        place.setKakaoPlaceId(valueOrDefault(request.kakaoPlaceId(), place.getKakaoPlaceId()));
        place.setName(valueOrDefault(request.name(), place.getName()));
        place.setTeam(valueOrDefault(request.team(), place.getTeam()));
        place.setTeamId(request.teamId() == 0 ? place.getTeamId() : request.teamId());
        String address = valueOrDefault(request.address(), place.getAddress());
        place.setDistrict(districtFromAddress(address));
        place.setAddress(address);
        place.setRoadAddress(valueOrDefault(request.roadAddress(), place.getRoadAddress()));
        place.setLatitude(request.latitude() == 0 ? place.getLatitude() : request.latitude());
        place.setLongitude(request.longitude() == 0 ? place.getLongitude() : request.longitude());
        place.setCategory(valueOrDefault(request.category(), place.getCategory()));
        place.setCategoryName(valueOrDefault(request.categoryName(), place.getCategoryName()));
        place.setCategoryGroupCode(valueOrDefault(request.categoryGroupCode(), place.getCategoryGroupCode()));
        place.setPhone(valueOrDefault(request.phone(), place.getPhone()));
        place.setKakaoPlaceUrl(valueOrDefault(request.kakaoPlaceUrl(), place.getKakaoPlaceUrl()));
        place.setNote(valueOrDefault(request.note(), place.getNote()));
        place.setStatus(valueOrDefault(request.status(), place.getStatus()));
        if (request.tags() != null) {
            place.setTags(request.tags());
        }
    }

    private void refreshPlaceRating(long placeId) {
        PlaceEntity place = findAnyPlace(placeId);
        List<ReviewEntity> activeReviews = reviewRepository.findByPlaceIdAndActive(placeId, true);
        double rating = activeReviews.stream().mapToInt(ReviewEntity::getRating).average().orElse(0.0);
        place.setRating(Math.round(rating * 10) / 10.0);
        place.setReviewCount(activeReviews.size());
        place.setUpdatedAt(now());
    }

    private PlaceEntity findActivePlace(long placeId) {
        PlaceEntity place = findAnyPlace(placeId);
        if (!ACTIVE.equals(place.getStatus())) {
            throw new ApiException(ErrorCode.PLACE_NOT_FOUND);
        }
        return place;
    }

    private PlaceEntity findAnyPlace(long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new ApiException(ErrorCode.PLACE_NOT_FOUND));
    }

    private ReportEntity findReportEntity(long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ApiException(ErrorCode.REPORT_NOT_FOUND));
    }

    private CrawlCandidateEntity findCrawlCandidateEntity(long candidateId) {
        return crawlCandidateRepository.findById(candidateId)
                .orElseThrow(() -> new ApiException(ErrorCode.CRAWL_CANDIDATE_NOT_FOUND));
    }

    private ReportEntity report(String placeName, String address, long teamId, String team, String content, String referenceLink) {
        ReportEntity report = new ReportEntity();
        report.setPlaceName(placeName);
        report.setAddress(address);
        report.setTeamId(teamId);
        report.setTeam(team);
        report.setContent(content);
        report.setReferenceLink(referenceLink);
        report.setCreatedAt(now());
        report.setStatus(PENDING);
        return report;
    }

    private Place toPlace(PlaceEntity place) {
        return new Place(
                place.getId(),
                place.getKakaoPlaceId(),
                place.getName(),
                place.getTeam(),
                place.getTeamId(),
                place.getDistrict(),
                place.getAddress(),
                place.getRoadAddress(),
                place.getLatitude(),
                place.getLongitude(),
                place.getCategory(),
                place.getCategoryName(),
                place.getCategoryGroupCode(),
                place.getPhone(),
                place.getKakaoPlaceUrl(),
                place.getRepresentativeImageUrl(),
                place.getNote(),
                place.getStatus(),
                place.getRating(),
                place.getReviewCount(),
                place.getDistanceMeters(),
                new ArrayList<>(place.getTags()),
                stringify(place.getCreatedAt()),
                stringify(place.getUpdatedAt())
        );
    }

    private Report toReport(ReportEntity report) {
        return new Report(report.getId(), report.getPlaceName(), report.getAddress(), report.getTeamId(), report.getTeam(), report.getContent(), report.getReferenceLink(), stringify(report.getCreatedAt()), report.getStatus(), report.getRejectReason());
    }

    private Review toReview(ReviewEntity review) {
        return new Review(review.getId(), review.getPlaceId(), review.getContent(), review.getRating(), stringify(review.getCreatedAt()), review.isActive());
    }

    private CrawlCandidate toCrawlCandidate(CrawlCandidateEntity candidate) {
        return new CrawlCandidate(candidate.getId(), candidate.getSource(), candidate.getSourceId(), candidate.getKeyword(), candidate.getName(), candidate.getAddress(), candidate.getRoadAddress(), candidate.getPhone(), candidate.getMapLink(), candidate.getCategoryName(), candidate.getCategoryGroupCode(), candidate.getLatitude(), candidate.getLongitude(), candidate.getDistanceMeters(), stringify(candidate.getCollectedAt()), candidate.getStatus(), candidate.getDuplicateReason(), candidate.getSourceUrl(), candidate.getDuplicateCheckResult(), new ArrayList<>(candidate.getSourceTeams()), candidate.getMatchScore());
    }

    private boolean matches(String expected, String actual) {
        return expected == null || expected.isBlank() || expected.equals(actual);
    }

    private boolean containsKeyword(String keyword, PlaceEntity place) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        String normalized = keyword.toLowerCase(Locale.ROOT);
        return contains(place.getName(), normalized)
                || contains(place.getDistrict(), normalized)
                || contains(place.getAddress(), normalized)
                || place.getTags().stream().anyMatch(tag -> contains(tag, normalized));
    }

    private boolean contains(String value, String normalizedKeyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    static String districtFromAddress(String address) {
        if (address == null || address.isBlank()) {
            return "미분류";
        }
        String[] parts = address.split(" ");
        return parts.length >= 2 ? parts[1] : "미분류";
    }

    static String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalize(String value) {
        return value == null ? "" : value.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    private String lastCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return "술집";
        }
        String[] parts = categoryName.split(">");
        return parts[parts.length - 1].trim();
    }

    private double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6_371_000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    static double toDouble(String value) {
        try {
            return value == null || value.isBlank() ? 0 : Double.parseDouble(clean(value));
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    static long toLong(String value) {
        try {
            return value == null || value.isBlank() ? 0 : Long.parseLong(clean(value));
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private int toInt(String value) {
        try {
            return value == null || value.isBlank() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private LocalDateTime parseCollectedAt(String value) {
        if (value == null || value.isBlank()) {
            return now();
        }
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            try {
                return OffsetDateTime.parse(value).toLocalDateTime();
            } catch (DateTimeParseException ignored) {
                throw new ApiException(ErrorCode.INVALID_REQUEST, "collectedAt은 ISO 8601 형식이어야 합니다.");
            }
        }
    }

    private String stringify(LocalDateTime value) {
        return value == null ? "" : value.toString();
    }

    static LocalDateTime now() {
        return LocalDateTime.now();
    }

    static String clean(String value) {
        return value == null ? "" : value.replace("\uFEFF", "").trim();
    }

}
