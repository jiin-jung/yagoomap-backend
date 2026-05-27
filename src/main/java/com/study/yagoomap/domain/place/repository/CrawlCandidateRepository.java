package com.study.yagoomap.domain.place.repository;

import com.study.yagoomap.domain.place.entity.CrawlCandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CrawlCandidateRepository extends JpaRepository<CrawlCandidateEntity, Long> {

    Optional<CrawlCandidateEntity> findBySourceAndSourceId(String source, String sourceId);

    List<CrawlCandidateEntity> findByStatus(String status);
}
