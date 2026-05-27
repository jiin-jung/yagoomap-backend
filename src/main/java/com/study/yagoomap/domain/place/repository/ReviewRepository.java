package com.study.yagoomap.domain.place.repository;

import com.study.yagoomap.domain.place.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByPlaceIdAndActive(long placeId, boolean active);
}
