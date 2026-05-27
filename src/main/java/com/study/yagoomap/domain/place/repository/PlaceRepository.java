package com.study.yagoomap.domain.place.repository;

import com.study.yagoomap.domain.place.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {

    Optional<PlaceEntity> findByKakaoPlaceId(String kakaoPlaceId);

    List<PlaceEntity> findByStatus(String status);
}
