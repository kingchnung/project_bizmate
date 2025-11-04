package com.bizmate.groupware.board.api;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardType;
import com.bizmate.groupware.board.dto.BoardDto;
import com.bizmate.groupware.board.repository.BoardRepository;
import com.bizmate.groupware.board.service.BoardService;
import com.bizmate.hr.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final BoardRepository boardRepository;

    // ✅ 사용자 게시판 목록 조회 + 검색(공지사항, 건의사항, 일반)
    @GetMapping
    public ResponseEntity<PageResponseDTO<BoardDto>> getBoardList(
            @ModelAttribute PageRequestDTO requestDTO,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(boardService.getBoardPage(requestDTO, user));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageResponseDTO<BoardDto>> getAllBoardsForAdmin(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<BoardDto> response = boardService.getBoardPageForAdmin(pageRequestDTO);
        return ResponseEntity.ok(response);
    }

    // ✅ 게시글 상세 조회
//    @GetMapping("/{boardNo}")
//    public ResponseEntity<BoardDto> getBoard(@PathVariable Long boardNo) {
//        BoardDto board = boardService.getBoard(boardNo);
//        return ResponseEntity.ok(board);
//    }
    // ✅ 게시글 상세 조회 (권한 포함 응답)
    @GetMapping("/{boardNo}")
    public ResponseEntity<BoardDto> getBoard(
            @PathVariable Long boardNo,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        BoardDto board = boardService.getBoard(boardNo, user); // ✅ user 전달
        return ResponseEntity.ok(board);
    }

    // ✅ 익명게시판 작성자 확인 (관리자 전용)
    @GetMapping("/{id}/author")
    public ResponseEntity<String> getAnonymousAuthor(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (!currentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("관리자만 열람 가능합니다.");
        }

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (board.getBoardType() != BoardType.SUGGESTION) {
            return ResponseEntity.badRequest().body("익명 게시판이 아닙니다.");
        }

        return ResponseEntity.ok(board.getAuthorName());
    }

    // ✅ 게시글 등록
    @PostMapping
    public ResponseEntity<BoardDto> createBoard(
            @RequestBody BoardDto dto,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        BoardDto saved = boardService.createBoard(dto, user);
        return ResponseEntity.ok(saved);
    }

    // ✅ 게시글 수정
    @PutMapping("/{boardNo}")
    public ResponseEntity<BoardDto> updateBoard(
            @PathVariable Long boardNo,
            @RequestBody BoardDto dto,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        BoardDto updated = boardService.updateBoard(boardNo, dto, user);
        return ResponseEntity.ok(updated);
    }

    // ✅ 게시글 삭제 (논리 삭제)
    @DeleteMapping("/{boardNo}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long boardNo,
            @RequestParam(defaultValue = "false") boolean isDelete,
            @AuthenticationPrincipal UserPrincipal user
    ) {

        boardService.deleteBoard(boardNo, user);
        return ResponseEntity.noContent().build();
    }

    // 공지사항 등록
    @PostMapping("/notice")
    public ResponseEntity<BoardDto> createNotice(@RequestBody BoardDto dto, @AuthenticationPrincipal UserPrincipal user) {
        if (!user.isAdmin()) {
            throw new AccessDeniedException("공지사항은 관리자만 작성할 수 있습니다.");
        }
        return ResponseEntity.ok(boardService.createBoard(dto, user));
    }
}
