package com.study.yagoomap.domain.place.repository;

import com.study.yagoomap.domain.place.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {

    List<PlaceEntity> findByStatus(String status);

    long countByStatus(String status);
}
