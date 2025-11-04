package com.bizmate.groupware.board.domain;

import com.bizmate.common.domain.BaseEntity;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.UserPrincipal;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BOARD")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;

    @Column(nullable = false)
    private String title;

    @Lob
    private String content;

    @Column(nullable = false)
    private String authorName;

    @Column(nullable = false)
    private String authorId;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;


    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

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
