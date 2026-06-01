package com.study.yagoomap.domain.notice.repository;

import com.study.yagoomap.domain.notice.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {}
