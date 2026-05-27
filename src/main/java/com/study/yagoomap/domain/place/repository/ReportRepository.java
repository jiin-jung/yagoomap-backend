package com.study.yagoomap.domain.place.repository;

import com.study.yagoomap.domain.place.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    List<ReportEntity> findByStatus(String status);
}
