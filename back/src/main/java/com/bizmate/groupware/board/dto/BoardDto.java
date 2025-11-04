package com.bizmate.groupware.board.dto;

import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Long boardNo;
    private BoardType boardType;
    private String title;
    private String content;
    private String authorId;
    private String authorName; // 표시용 (익명 처리 포함)
    private boolean isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<CommentDto> comments;
    // ✅ 추가: 프론트에서 버튼 노출 제어용
    private boolean canEdit;
    private boolean canDelete;

    public static BoardDto fromEntity(Board entity) {
        return BoardDto.builder()
                .boardNo(entity.getBoardNo())
                .boardType(entity.getBoardType())
                .title(entity.getTitle())
                .content(entity.getContent())
                .authorName(entity.getAuthorName())
                .authorId(entity.getAuthorId())
                .isDeleted(entity.isDeleted())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .comments(entity.getComments() != null
                        ? entity.getComments().stream()
                        .filter(c -> !c.isDeleted()) // 논리삭제 제외
                        .map(CommentDto::fromEntity)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }

    public Board toEntity() {
        Board board = new Board();
        board.setBoardType(this.boardType);
        board.setTitle(this.title);
        board.setContent(this.content);
        board.setAuthorName(this.authorName);
        board.setAuthorId(this.authorId);
        board.setDeleted(this.isDeleted);
        return board;
    }
}
