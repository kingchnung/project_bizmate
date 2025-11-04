package com.bizmate.groupware.board.service;

import com.bizmate.common.exception.ForbiddenOperationException;
import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardType;
import com.bizmate.groupware.board.domain.Comment;
import com.bizmate.groupware.board.dto.BoardDto;
import com.bizmate.groupware.board.repository.BoardRepository;
import com.bizmate.groupware.board.repository.CommentRepository;
import com.bizmate.hr.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    //게시글 등록
    @Override
    @Transactional
    public BoardDto createBoard(BoardDto dto, UserPrincipal user) {
        if (!isAdmin(user) && dto.getBoardType() == BoardType.NOTICE) {
            throw new ForbiddenOperationException("공지사항은 관리자만 작성할 수 있습니다.");
        }

        Board board = new Board();
        board.setBoardType(dto.getBoardType());
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());

        // ✅ 항상 실명 저장 (DB에는 실제 이름이 남아야 함)
        board.setAuthorId(user.getUsername());
        board.setAuthorName(user.getEmpName());

        board.setDeleted(false);
        board.markCreated(user);

        Board saved = boardRepository.saveAndFlush(board);

        // ✅ 응답에서는 사용자 권한에 따라 익명/실명 처리
        return toDtoForUser(saved, user);
    }

    //게시글 삭제
    @Override
    @Transactional
    public void deleteBoard(Long boardNo, UserPrincipal currentUser) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        // 공지사항은 관리자만 삭제 가능
        if (board.getBoardType() == BoardType.NOTICE && !currentUser.isAdmin()) {
            throw new ForbiddenOperationException("공지사항은 관리자만 삭제할 수 있습니다.");
        }

        // 일반 사용자는 본인 글만 논리삭제 가능
        if (!currentUser.isAdmin()) {
            if (!board.getAuthorId().equals(currentUser.getUsername())) {
                throw new ForbiddenOperationException("본인 글만 삭제 가능합니다.");
            }
            board.setDeleted(true);
            return;
        }

        // 관리자면: 논리삭제 시 isDeleted=true면 물리삭제
        if (board.isDeleted()) {
            boardRepository.delete(board);  // 물리삭제
        } else {
            board.markUpdated(currentUser);
            board.setDeleted(true);         // 논리삭제 1단계
        }
    }

    //댓글 등록
    @Transactional
    @Override
    public Comment addComment(Long boardId, String content, UserPrincipal user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 공지사항은 댓글 금지
        if (board.getBoardType() == BoardType.NOTICE) {
            throw new VerificationFailedException("공지사항에는 댓글을 달 수 없습니다.");
        }

        String displayName = board.getBoardType() == BoardType.SUGGESTION ? "익명" : user.getEmpName();

        Comment comment = Comment.builder()
                .board(board)
                .content(content)
                .authorId(user.getUsername())
                .authorName(displayName)
                .build();

        comment.markCreated(user);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public List<BoardDto> getBoardsByType(BoardType type) {
        return boardRepository.findByBoardTypeAndIsDeletedFalse(type)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 게시물 상세정보
//    @Override
//    @Transactional
//    public BoardDto getBoard(Long id) {
//        Board board = boardRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
//        return toDto(board);
//    }
    // 게시물 상세정보 (권한 포함)
    @Override
    @Transactional
    public BoardDto getBoard(Long id, UserPrincipal user) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        return toDto(board, user); // ✅ 유저 기반 권한 계산
    }

    // 게시물 수정
    @Override
    @Transactional
    public BoardDto updateBoard(Long id, BoardDto dto, UserPrincipal user) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 직접 권한 검사
        boolean isAdmin = user.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .anyMatch(a -> a.equals("ROLE_CEO") || a.equals("ROLE_ADMIN") || a.equals("sys:admin"));

        boolean isAuthor = board.getAuthorId().equals(user.getUsername());

        // 공지사항 수정: 관리자만
        if (board.getBoardType() == BoardType.NOTICE && !isAdmin) {
            throw new ForbiddenOperationException("공지사항은 관리자만 수정할 수 있습니다.");
        }

        // 일반 게시글 수정: 작성자 본인만
        if (!isAdmin && !isAuthor) {
            throw new ForbiddenOperationException("본인 글만 수정할 수 있습니다.");
        }

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.markUpdated(user);

        return toDto(board);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BoardDto> getBoardPage(PageRequestDTO req, UserPrincipal user) {
        Pageable pageable = PageRequest.of(
                Math.max(0, req.getPage() - 1),
                req.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // 🔧 여기서 보정: 프론트는 'type'만 보냄 → Enum으로 변환
        BoardType bt = req.getBoardType();
        if (bt == null) {
            String t = req.getType(); // "ALL" | "NOTICE" | "GENERAL" | "SUGGESTION" or null
            if (t != null && !t.isBlank() && !"ALL".equalsIgnoreCase(t)) {
                bt = BoardType.from(t);  // @JsonCreator from(String) 이미 있음
            } else {
                bt = null; // ALL 이면 null로 두어 전체 조회
            }
        }

        Page<Board> result = boardRepository.findActiveByKeyword(
                emptyToNull(req.getKeyword()),
                bt,                                  // ← 보정된 Enum 사용!
                emptyToAll(req.getSearchType()),
                pageable
        );

        List<BoardDto> dtoList = result.getContent().stream()
                .map(b -> toDtoForUser(b, user))
                .toList();

        return PageResponseDTO.<BoardDto>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(req)
                .totalCount(result.getTotalElements())
                .build();
    }

    private String emptyToAll(String v) { return (v == null || v.isBlank()) ? "ALL" : v; }
    private String emptyToNull(String v) { return (v == null || v.isBlank()) ? null : v; }


    @Override
    @Transactional
    public PageResponseDTO<BoardDto> getBoardPageForAdmin(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Board> result = boardRepository.findAllByKeyword(pageRequestDTO.getKeyword(), pageable);

        List<BoardDto> dtoList = result.getContent().stream()
                .map(BoardDto::fromEntity)
                .toList();

        return PageResponseDTO.<BoardDto>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    // 권한/작성자 판단 공통 유틸  (NPE 방지)
    private boolean isAdmin(UserPrincipal user) {
        if (user == null || user.getAuthorities() == null) return false;
        return user.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth) || "ROLE_CEO".equals(auth) || "sys:admin".equals(auth));
    }

    private boolean isAuthor(Board board, UserPrincipal user) {
        if (user == null) return false;
        return board.getAuthorId() != null && board.getAuthorId().equals(user.getUsername());
    }

    private boolean isNotice(Board board) {
        return board.getBoardType() == BoardType.NOTICE;
    }

    // 상세 조회 매핑: user를 받아 계산해서 DTO에만 세팅
    private BoardDto toDto(Board board, UserPrincipal user) {
        boolean admin = isAdmin(user);
        boolean author = isAuthor(board, user);
        boolean notice = isNotice(board);

        return BoardDto.builder()
                .boardNo(board.getBoardNo())
                .boardType(board.getBoardType())
                .title(board.getTitle())
                .content(board.getContent())
                .authorId(board.getAuthorId())     // ← 엔티티에 있으니 그대로 넣기
                .authorName(board.getAuthorName())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .isDeleted(board.isDeleted())

                // ← 엔티티에 없는 계산 필드: DTO에만 넣기
                .canEdit(admin || (author && !notice))
                .canDelete(admin || author)
                .build();
    }

    private BoardDto toDto(Board board) {
        return BoardDto.builder()
                .boardNo(board.getBoardNo())
                .boardType(board.getBoardType())
                .title(board.getTitle())
                .content(board.getContent())
                .authorId(board.getAuthorId()) // 반드시 추가
                .authorName(board.getAuthorName())
                .isDeleted(board.isDeleted())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .canEdit(false)    // User 정보 없을 땐 false 기본값
                .canDelete(false)  // User 정보 없을 땐 false 기본값
                .build();
    }

    // BoardServiceImpl.java 안에
    private BoardDto toDtoForUser(Board board, UserPrincipal user) {
        boolean admin = isAdmin(user);
        boolean author = isAuthor(board, user);
        boolean isSuggestion = board.getBoardType() == BoardType.SUGGESTION;
        boolean isNotice = board.getBoardType() == BoardType.NOTICE;

        // ✅ 익명게시판은 일반사용자에게만 "익명"으로 표시
        String displayName = isSuggestion
                ? (admin ? board.getAuthorName() : "익명")
                : board.getAuthorName();

        // ✅ 관리자에게는 authorId 노출, 일반 유저는 null 처리
        String authorId = admin ? board.getAuthorId() : null;

        return BoardDto.builder()
                .boardNo(board.getBoardNo())
                .boardType(board.getBoardType())
                .title(board.getTitle())
                .content(board.getContent())
                .authorName(displayName)
                .authorId(authorId)
                .isDeleted(board.isDeleted())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .canEdit(admin || (author && !isNotice))
                .canDelete(admin || author)
                .build();
    }
}
