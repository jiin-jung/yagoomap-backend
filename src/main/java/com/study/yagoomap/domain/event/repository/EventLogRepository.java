package com.study.yagoomap.domain.event.repository;

import com.study.yagoomap.domain.event.EventType;
import com.study.yagoomap.domain.event.entity.EventLogEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventLogRepository extends JpaRepository<EventLogEntity, Long> {

    long countByCreatedAtGreaterThanEqual(LocalDateTime from);

    long countByTypeAndCreatedAtGreaterThanEqual(EventType type, LocalDateTime from);

    @Query("select count(distinct e.sessionId) from EventLogEntity e "
            + "where e.sessionId is not null and e.createdAt >= :from")
    long countDistinctSessionsSince(@Param("from") LocalDateTime from);

    @Query("select e.type, count(e) from EventLogEntity e "
            + "where e.createdAt >= :from group by e.type order by count(e) desc")
    List<Object[]> countByTypeSince(@Param("from") LocalDateTime from);

    @Query("select e.placeId, count(e) from EventLogEntity e "
            + "where e.type = :type and e.placeId is not null and e.createdAt >= :from "
            + "group by e.placeId order by count(e) desc")
    List<Object[]> topPlacesSince(@Param("type") EventType type, @Param("from") LocalDateTime from, Pageable pageable);

    @Query("select e.keyword, count(e) from EventLogEntity e "
            + "where e.type = :type and e.keyword is not null and e.keyword <> '' and e.createdAt >= :from "
            + "group by e.keyword order by count(e) desc")
    List<Object[]> topKeywordsSince(@Param("type") EventType type, @Param("from") LocalDateTime from, Pageable pageable);

    @Query("select coalesce(e.referrer, 'direct'), count(e) from EventLogEntity e "
            + "where e.createdAt >= :from group by coalesce(e.referrer, 'direct') order by count(e) desc")
    List<Object[]> countByReferrerSince(@Param("from") LocalDateTime from);

    @Query("select cast(e.createdAt as date), count(e) from EventLogEntity e "
            + "where e.createdAt >= :from group by cast(e.createdAt as date) order by cast(e.createdAt as date)")
    List<Object[]> dailyTrendSince(@Param("from") LocalDateTime from);

    /** 일별 순방문자(DAU): session_id 기준 unique 카운트 */
    @Query("select cast(e.createdAt as date), count(distinct e.sessionId) from EventLogEntity e "
            + "where e.sessionId is not null and e.createdAt >= :from "
            + "group by cast(e.createdAt as date) order by cast(e.createdAt as date)")
    List<Object[]> dailyDauTrendSince(@Param("from") LocalDateTime from);
}
