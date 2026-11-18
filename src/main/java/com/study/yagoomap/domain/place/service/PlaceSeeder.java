package com.study.yagoomap.domain.place.service;

import com.study.yagoomap.domain.place.dto.CrawlCandidateRequest;
import com.study.yagoomap.domain.place.dto.ReportRequest;
import com.study.yagoomap.domain.place.dto.ReviewRequest;
import com.study.yagoomap.domain.place.entity.PlaceEntity;
import com.study.yagoomap.domain.place.repository.PlaceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class PlaceSeeder {

    private static final String ACTIVE = "ACTIVE";
    private static final String PLACES_CSV = "/seed/places.csv";

    private final PlaceRepository placeRepository;
    private final PlaceService placeService;
    private final Environment environment;
    private final boolean csvSeedEnabled;

    public PlaceSeeder(
            PlaceRepository placeRepository,
            PlaceService placeService,
            Environment environment,
            @Value("${yagoomap.seed.csv.enabled:true}") boolean csvSeedEnabled
    ) {
        this.placeRepository = placeRepository;
        this.placeService = placeService;
        this.environment = environment;
        this.csvSeedEnabled = csvSeedEnabled;
    }

    @PostConstruct
    void seed() {
        if (csvSeedEnabled) {
            importPlacesCsv();
        }

        if (environment.acceptsProfiles(Profiles.of("prod")) || placeRepository.count() > 0) {
            return;
        }

        long jamsil = saveSample("잠실 야구포차", "LG 트윈스", 1L, "서울 송파구 올림픽로 25", 37.5122, 127.0719, "포차", "02-0000-0001", "잠실구장 근처에서 LG 경기 관람 수요가 높은 콘셉트 샘플 장소입니다.", 4.8, 128, 150, List.of("대형 TV", "단체석", "잠실구장 근처"));
        saveSample("트윈스 라운지 신천", "LG 트윈스", 1L, "서울 송파구 백제고분로7길 32", 37.5109, 127.0842, "펍", "02-0000-0002", "경기일 단체 응원 모임에 맞춘 펍 형태의 샘플 장소입니다.", 4.6, 87, 400, List.of("예약 추천", "맥주", "응원 분위기"));
        long gangnam = saveSample("엘지 응원집 강남", "LG 트윈스", 1L, "서울 강남구 강남대로 396", 37.4979, 127.0276, "호프", "02-0000-0003", "퇴근 후 야구를 보기 좋은 강남권 샘플 장소입니다.", 4.9, 94, 500, List.of("역세권", "치킨", "퇴근 후"));
        saveSample("홍대 야구상회", "LG 트윈스", 1L, "서울 마포구 어울마당로 94", 37.5535, 126.9220, "술집", "02-0000-0004", "젊은 팬층과 SNS 공유를 고려한 홍대권 샘플 장소입니다.", 4.7, 62, 300, List.of("SNS", "하이라이트 상영", "2차"));
        saveSample("종로 트윈스비어", "LG 트윈스", 1L, "서울 종로구 종로 51", 37.5702, 126.9830, "비어바", "02-0000-0005", "도심 약속과 경기 관람을 함께 잡는 콘셉트의 샘플 장소입니다.", 4.8, 76, 250, List.of("도심", "스크린", "모임"));

        placeService.createReport(new ReportRequest("대전 이글스펍", "대전 중구 중앙로 1", 2L, "한화 이글스", "한화 팬이 많이 모입니다.", "https://map.naver.com/"));
        placeService.createReport(new ReportRequest("사직 자이언츠비어", "부산 동래구 사직로 45", 3L, "롯데 자이언츠", "사직 경기 중계를 자주 틀어줍니다.", ""));

        placeService.createReview(jamsil, new ReviewRequest("화면이 크고 단체 응원 분위기가 좋습니다.", 5));
        placeService.createReview(jamsil, new ReviewRequest("경기 시작 전 예약하는 편이 안전합니다.", 4));
        placeService.createReview(gangnam, new ReviewRequest("강남에서 퇴근 후 보기 편했습니다.", 5));

        placeService.createCrawlCandidate(new CrawlCandidateRequest("야구 중계 술집 잠실", "잠실 야구포차 후보", "서울 송파구 올림픽로 25", "02-0000-9999", "https://map.naver.com/"));
    }

    private void importPlacesCsv() {
        try (InputStream stream = getClass().getResourceAsStream(PLACES_CSV)) {
            if (stream == null) {
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                reader.readLine();
                // 줄마다 전체 조회하지 않도록 한 번만 불러오고, 새로 저장한 장소를 이어 붙인다.
                List<PlaceEntity> existing = new ArrayList<>(placeService.duplicateCandidates());
                String line;
                while ((line = reader.readLine()) != null) {
                    saveCsvPlace(parseCsvLine(line), existing);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("장소 CSV를 읽을 수 없습니다.", exception);
        }
    }

    private void saveCsvPlace(List<String> row, List<PlaceEntity> existing) {
        if (row.size() < 12 || PlaceService.isBlank(row.get(0))) {
            return;
        }

        String name = PlaceService.clean(row.get(0));
        String address = PlaceService.clean(row.get(1));
        double latitude = PlaceService.toDouble(row.get(2));
        double longitude = PlaceService.toDouble(row.get(3));
        String team = PlaceService.clean(row.get(4));
        long teamId = PlaceService.toLong(row.get(5));
        String categoryName = PlaceService.clean(row.get(6));
        String category = PlaceService.clean(row.get(7));
        String phone = PlaceService.clean(row.get(8));
        String note = PlaceService.clean(row.get(9));
        List<String> tags = splitTags(row.get(10));
        String status = PlaceService.valueOrDefault(PlaceService.clean(row.get(11)), ACTIVE);

        if (placeService.isDuplicate(existing, name, address, phone, latitude, longitude)) {
            return;
        }

        PlaceEntity place = newPlace(name, team, teamId, address, latitude, longitude, category, phone, note, tags);
        place.setCategoryName(categoryName);
        place.setStatus(status);
        placeRepository.save(place);
        if (!"INACTIVE".equals(status)) {
            existing.add(place);
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '"') {
                if (quoted && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append(character);
                    index++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        values.add(current.toString());
        return values;
    }

    private List<String> splitTags(String value) {
        if (PlaceService.isBlank(value)) {
            return List.of();
        }
        List<String> tags = new ArrayList<>();
        for (String tag : value.split(",")) {
            String cleaned = PlaceService.clean(tag);
            if (!cleaned.isBlank()) {
                tags.add(cleaned);
            }
        }
        return tags;
    }

    private long saveSample(String name, String team, long teamId, String address, double latitude, double longitude, String category, String phone, String note, double rating, int reviewCount, int distanceMeters, List<String> tags) {
        PlaceEntity place = newPlace(name, team, teamId, address, latitude, longitude, category, phone, note, tags);
        place.setNaverMapUrl("https://map.naver.com/");
        place.setRating(rating);
        place.setReviewCount(reviewCount);
        place.setDistanceMeters(distanceMeters);
        return placeRepository.save(place).getId();
    }

    private PlaceEntity newPlace(String name, String team, long teamId, String address, double latitude, double longitude, String category, String phone, String note, List<String> tags) {
        PlaceEntity place = new PlaceEntity();
        place.setName(name);
        place.setTeam(team);
        place.setTeamId(teamId);
        place.setDistrict(PlaceService.districtFromAddress(address));
        place.setAddress(address);
        place.setLatitude(latitude);
        place.setLongitude(longitude);
        place.setCategory(category);
        place.setPhone(phone);
        place.setInstagramUrl("");
        place.setNaverMapUrl("");
        place.setRepresentativeImageUrl("");
        place.setPhotos(List.of());
        place.setNote(note);
        place.setStatus(ACTIVE);
        place.setRating(0.0);
        place.setReviewCount(0);
        place.setDistanceMeters(0);
        place.setTags(tags);
        place.setCreatedAt(PlaceService.now());
        place.setUpdatedAt(PlaceService.now());
        return place;
    }
}
