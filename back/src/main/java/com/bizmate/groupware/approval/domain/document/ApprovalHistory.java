package com.bizmate.groupware.approval.domain.document;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "approval_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doc_id", nullable = false)
    private String docId;

    @Column(name = "actor_user_id", nullable = false)
    private Long actorUserId;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; //등록, 결재, 반려, 삭제 등

    @Column(name = "action_comment")
    private String actionComment;

    @Column(name = "action_timestamp", nullable = false)
    private LocalDateTime actionTimestamp;
}
