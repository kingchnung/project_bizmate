package com.bizmate.groupware.board.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ✅ 댓글 이력 테이블 (COMMENT_HISTORY)
 */
@Entity
@Table(name = "COMMENT_HISTORY")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "COMMENT_NO", nullable = false)
    private Long commentNo;

    @Column(name = "BOARD_NO", nullable = false)
    private Long boardNo;

    @Column(name = "ACTOR_USER_ID", nullable = false)
    private String actorUserId;

    @Column(name = "ACTION_TYPE", nullable = false, length = 30)
    private String actionType; // 등록, 수정, 삭제

    @Column(name = "ACTION_COMMENT")
    private String actionComment;

    @Column(name = "ACTION_TIMESTAMP", nullable = false)
    private LocalDateTime actionTimestamp;
}