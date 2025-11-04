package com.bizmate.groupware.board.domain;

import com.bizmate.common.domain.BaseEntity;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.UserPrincipal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "BOARD_COMMENT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no")
    private Board board;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private String authorId;

    @Column(nullable = false)
    private String authorName; //익명 게시판의 경우 '익명' 표시

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Builder.Default
    private boolean anonymous = false;

    public void markCreated(UserPrincipal user) {
        super.setCreatedBy(user.getEmpName());
        super.setUpdatedBy(user.getEmpName());
        super.setCreatedAt(LocalDateTime.now());
        super.setUpdatedAt(LocalDateTime.now());
    }

    public void markUpdated(UserPrincipal user) {
        super.setUpdatedBy(user.getEmpName());
        super.setUpdatedAt(LocalDateTime.now());
    }
}
