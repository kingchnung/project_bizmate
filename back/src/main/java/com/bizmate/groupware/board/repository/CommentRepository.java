package com.bizmate.groupware.board.repository;

import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBoard_BoardNoAndIsDeletedFalse(Long boardNo);
}
