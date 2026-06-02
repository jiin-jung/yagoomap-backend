package com.study.yagoomap.domain.game.repository;

import com.study.yagoomap.domain.game.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<GameEntity, Long> {

    Optional<GameEntity> findByGameId(String gameId);

    List<GameEntity> findByGameDateOrderByStartTimeAsc(LocalDate gameDate);
}
