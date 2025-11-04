package com.bizmate.groupware.board.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "BOARD_HISTORY")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BOARD_NO", nullable = false)
    private Long boardNo;

    @Column(name = "actor_user_id", nullable = false)
    private String actorUserId;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(name = "action_comment")
    private String actionComment;

    @Column(name = "action_timestamp", nullable = false)
    private LocalDateTime actionTimestamp;
}
