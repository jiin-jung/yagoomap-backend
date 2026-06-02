package com.study.yagoomap.domain.game.service;

import com.study.yagoomap.domain.game.dto.GameResponse;
import com.study.yagoomap.domain.game.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 경기 일정 조회(읽기 전용).
 */
@Service
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameResponse> gamesByDate(LocalDate date) {
        return gameRepository.findByGameDateOrderByStartTimeAsc(date).stream()
                .map(GameResponse::from)
                .toList();
    }

    public List<GameResponse> todayGames() {
        return gamesByDate(LocalDate.now());
    }
}
