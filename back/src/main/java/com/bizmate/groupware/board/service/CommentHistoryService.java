package com.bizmate.groupware.board.service;

import com.bizmate.groupware.board.domain.Comment;
import com.bizmate.groupware.board.domain.CommentHistory;
import com.bizmate.groupware.board.repository.CommentHistoryRepository;
import com.bizmate.hr.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * ✅ 댓글 이력 저장 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentHistoryService {

    private final CommentHistoryRepository commentHistoryRepository;

    @Transactional
    public void saveHistory(Comment comment, UserPrincipal user, String actionType, String commentText) {
        CommentHistory history = CommentHistory.builder()
                .commentNo(comment.getCommentNo())
                .boardNo(comment.getBoard().getBoardNo())
                .actorUserId(user.getUsername())
                .actionType(actionType)
                .actionComment(commentText)
                .actionTimestamp(LocalDateTime.now())
                .build();

        commentHistoryRepository.save(history);
        log.info("🧾 [CommentHistory] {} | commentNo={} | by={} | at={}",
                actionType, comment.getCommentNo(), user.getUsername(), history.getActionTimestamp());
    }
}