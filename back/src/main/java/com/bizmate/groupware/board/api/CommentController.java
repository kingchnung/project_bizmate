package com.bizmate.groupware.board.api;

import com.bizmate.groupware.board.dto.CommentDto;
import com.bizmate.groupware.board.service.CommentService;
import com.bizmate.hr.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardNo}/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    // ✅ 댓글 목록 조회
    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long boardNo, @AuthenticationPrincipal UserPrincipal user) {
        List<CommentDto> comments = commentService.getComments(boardNo, user);
        return ResponseEntity.ok(comments);
    }

    // ✅ 댓글 등록
    @PostMapping
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long boardNo,
            @RequestBody CommentDto dto,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        CommentDto saved = commentService.addComment(boardNo, dto.getContent(), user);
        return ResponseEntity.ok(saved);
    }

    // ✅ 댓글 삭제
    @DeleteMapping("/{commentNo}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentNo,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        commentService.deleteComment(commentNo, user);
        return ResponseEntity.noContent().build();
    }
}
