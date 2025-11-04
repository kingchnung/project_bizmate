package com.bizmate.groupware.board.repository;

import com.bizmate.groupware.board.domain.CommentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentHistoryRepository extends JpaRepository<CommentHistory, Long> {
}
