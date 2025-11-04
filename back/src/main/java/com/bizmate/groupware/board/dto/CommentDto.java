package com.bizmate.groupware.board.dto;

import com.bizmate.groupware.board.domain.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long commentNo;
    private Long boardNo;
    private String content;
    private String authorName; // 표시용 (익명 처리 포함)
    private String authorId;
    private boolean anonymous;
    private boolean isDeleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentDto fromEntity(Comment entity) {
        return CommentDto.builder()
                .commentNo(entity.getCommentNo())
                .boardNo(entity.getBoard().getBoardNo())
                .content(entity.isDeleted() ? "(삭제된 댓글입니다)" : entity.getContent())
                .authorName(entity.isAnonymous() ? "익명" : entity.getAuthorName())
                .authorId(entity.getAuthorId())
                .anonymous(entity.isAnonymous())
                .isDeleted(entity.isDeleted())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Comment toEntity() {
        Comment comment = new Comment();
        comment.setCommentNo(this.commentNo);
        comment.setContent(this.content);
        comment.setAuthorName(this.authorName);
        comment.setAuthorId(this.authorId);
        comment.setAnonymous(this.anonymous);
        comment.setDeleted(this.isDeleted);
        return comment;
    }
}
