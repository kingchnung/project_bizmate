package com.bizmate.groupware.board.repository;

import com.bizmate.groupware.board.domain.BoardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardHistoryRepository extends JpaRepository<BoardHistory, Long> {
    List<BoardHistory> findByBoardNoOrderByActionTimestampDesc(Long boardNo);
}
