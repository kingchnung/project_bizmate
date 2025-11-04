package com.bizmate.groupware.board.service;

import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.Comment;
import com.bizmate.groupware.board.dto.CommentDto;
import com.bizmate.groupware.board.repository.BoardRepository;
import com.bizmate.groupware.board.repository.CommentRepository;
import com.bizmate.hr.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    /**
     * ✅ 댓글 등록
     */
    @Override
    @Transactional
    public CommentDto addComment(Long boardNo, String content, UserPrincipal user) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        // 익명 게시판 여부 체크
        boolean isAnonymous = board.getBoardType().name().equalsIgnoreCase("SUGGESTION");

        Comment comment = Comment.builder()
                .board(board)
                .content(content)
                .authorId(user.getUsername())
                .authorName(user.getEmpName())
                .anonymous(isAnonymous)
                .isDeleted(false)
                .build();

        Comment saved = commentRepository.save(comment);
        log.info("💬 댓글 등록 완료: {}", saved.getContent());

        return CommentDto.fromEntity(saved);
    }

    /**
     * ✅ 댓글 삭제
     */
    @Override
    @Transactional
    public void deleteComment(Long id, UserPrincipal user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 삭제 권한 체크 (본인 또는 관리자만)
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_CEO"));

        if (!user.getUsername().equals(comment.getAuthorId()) && !isAdmin) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
        log.info("🗑️ 댓글 논리삭제 완료: {}", id);
    }

    /**
     * ✅ 댓글 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long boardNo, UserPrincipal user) {
        boolean admin = isAdmin(user);
        return commentRepository.findByBoard_BoardNoAndIsDeletedFalse(boardNo)
                .stream()
                .map(c -> toDto(c, admin))
                .collect(Collectors.toList());
    }

    // CommentServiceImpl.java
    private boolean isAdmin(UserPrincipal user) {
        if (user == null || user.getAuthorities() == null) return false;
        return user.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_CEO") || role.equals("sys:admin"));
    }

    private CommentDto toDto(Comment entity, boolean admin) {
        String displayName = (entity.isAnonymous() && !admin) ? "익명" : entity.getAuthorName();
        return CommentDto.builder()
                .commentNo(entity.getCommentNo())
                .boardNo(entity.getBoard().getBoardNo())
                .content(entity.isDeleted() ? "(삭제된 댓글입니다)" : entity.getContent())
                .authorName(displayName)           // ✅ 관리자면 실명, 아니면 익명
                .authorId(entity.getAuthorId())    // ✅ 본인 여부 판단용으로 필요
                .anonymous(entity.isAnonymous())
                .isDeleted(entity.isDeleted())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
