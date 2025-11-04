//package com.bizmate.groupware.board.service;
//
//import com.bizmate.common.exception.VerificationFailedException;
//import com.bizmate.groupware.board.domain.Board;
//import com.bizmate.groupware.board.domain.BoardType;
//import com.bizmate.groupware.board.domain.Comment;
//import com.bizmate.groupware.board.dto.BoardDto;
//import com.bizmate.groupware.board.repository.BoardRepository;
//import com.bizmate.groupware.board.repository.CommentRepository;
//import com.bizmate.hr.security.UserPrincipal;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//
//@Slf4j
//@SpringBootTest
//@Transactional
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class BoardServiceTests {
//
//    @Autowired
//    private BoardService boardService;
//
//    @Autowired
//    private BoardRepository boardRepository;
//
//    @Autowired
//    private CommentRepository commentRepository;
//
//    private UserPrincipal adminUser;
//    private UserPrincipal normalUser;
//
//    @BeforeEach
//    void setup() {
//        adminUser = new UserPrincipal(
//                1L,                // userId
//                1001L,             // empId
//                "admin",           // username
//                "admin1234",       // pwHash (임시 값)
//                true,              // active
//                false,             // locked
//                List.of()          // authorities (비워둬도 OK)
//        );
//        adminUser.setEmpName("관리자");
//        adminUser.setEmail("admin@bizmate.com");
//
//        normalUser = new UserPrincipal(
//                2L,                // userId
//                2001L,             // empId
//                "user01",          // username
//                "userpw",          // pwHash
//                true,              // active
//                false,             // locked
//                List.of()
//        );
//        normalUser.setEmpName("김찬웅");
//        normalUser.setEmail("user01@bizmate.com");
//    }
//
//    /* ✅ 1️⃣ 게시글 등록 테스트 */
//    @Test
//    @Order(1)
//    @DisplayName("게시글 등록 - 일반/익명/공지사항 구분")
//    void createBoardTest() {
//        // 일반 게시글
//        BoardDto generalDto = BoardDto.builder()
//                .boardType(BoardType.GENERAL)
//                .title("일반 게시글 테스트")
//                .content("일반 게시글의 내용입니다.")
//                .build();
//
//        // 익명 건의
//        BoardDto suggestionDto = BoardDto.builder()
//                .boardType(BoardType.SUGGESTION)
//                .title("익명 건의사항")
//                .content("사무실 커피머신이 자주 고장납니다.")
//                .build();
//
//        // 공지사항
//        BoardDto noticeDto = BoardDto.builder()
//                .boardType(BoardType.NOTICE)
//                .title("공지사항 게시글")
//                .content("10월 25일 전사 워크숍 예정입니다.")
//                .build();
//
//        BoardDto general = boardService.createBoard(generalDto, normalUser);
//        BoardDto suggestion = boardService.createBoard(suggestionDto, normalUser);
//        BoardDto notice = boardService.createBoard(noticeDto, adminUser);
//
//        assertThat(general.getTitle()).isEqualTo("일반 게시글 테스트");
//        assertThat(suggestion.getAuthorName()).isEqualTo("익명");
//        assertThat(notice.getBoardType()).isEqualTo(BoardType.NOTICE);
//
//        log.info("✅ 게시글 등록 성공: general={}, suggestion={}, notice={}",
//                general.getBoardNo(), suggestion.getBoardNo(), notice.getBoardNo());
//    }
//
//    /* ✅ 2️⃣ 게시글 조회 테스트 */
//    @Test
//    @Order(2)
//    @DisplayName("게시글 단일 조회")
//    void getBoardTest() {
//        Board board = boardRepository.save(Board.builder()
//                .boardType(BoardType.GENERAL)
//                .title("단일 조회 테스트")
//                .content("단일 게시글 내용")
//                .authorName("테스터")
//                .authorId("5021002")
//                .build());
//
//        BoardDto dto = boardService.getBoard(board.getBoardNo());
//
//        assertThat(dto.getTitle()).isEqualTo("단일 조회 테스트");
//        assertThat(dto.getBoardType()).isEqualTo(BoardType.GENERAL);
//        log.info("🔍 게시글 조회 성공 → {}", dto);
//    }
//
//    /* ✅ 3️⃣ 게시글 수정 테스트 */
//    @Test
//    @Order(3)
//    @DisplayName("게시글 수정")
//    void updateBoardTest() {
//        Board saved = boardRepository.save(Board.builder()
//                .boardType(BoardType.GENERAL)
//                .title("수정 전 제목")
//                .content("수정 전 내용")
//                .authorName("홍길동")
//                .authorId("5033002")
//                .build());
//
//        BoardDto updateDto = BoardDto.builder()
//                .title("수정된 제목")
//                .content("수정된 내용")
//                .build();
//
//        BoardDto updated = boardService.updateBoard(saved.getBoardNo(), updateDto, normalUser);
//
//        assertThat(updated.getTitle()).isEqualTo("수정된 제목");
//        assertThat(updated.getContent()).isEqualTo("수정된 내용");
//        log.info("✏️ 게시글 수정 성공 → {}", updated);
//    }
//
//    /* ✅ 4️⃣ 게시글 논리삭제 테스트 */
//    @Test
//    @Order(4)
//    @DisplayName("게시글 논리삭제")
//    void deleteBoardTest() {
//        Board board = boardRepository.save(Board.builder()
//                .boardType(BoardType.GENERAL)
//                .title("삭제 테스트 글")
//                .content("삭제 전 내용")
//                .authorName("이수정")
//                .authorId("5021003")
//                .build());
//
//        boardService.deleteBoard(board.getBoardNo(), adminUser);
//
//        Board deleted = boardRepository.findById(board.getBoardNo())
//                .orElseThrow(() -> new RuntimeException("삭제 실패"));
//        assertThat(deleted.isDeleted()).isTrue();
//        log.info("🗑️ 논리삭제 완료 → {}", deleted.getTitle());
//    }
//
//    /* ✅ 5️⃣ 댓글 등록 테스트 */
//    @Test
//    @Order(5)
//    @DisplayName("댓글 등록 - 일반 / 익명 / 공지사항 예외")
//    void addCommentTest() {
//        // 일반 게시글
//        Board general = boardRepository.save(Board.builder()
//                .boardType(BoardType.GENERAL)
//                .title("댓글 테스트용 게시글")
//                .content("댓글 테스트 중입니다.")
//                .authorName("김유진")
//                .authorId("5022003")
//                .build());
//
//        Comment comment1 = boardService.addComment(general.getBoardNo(), "첫 번째 댓글", normalUser);
//        assertThat(comment1.getContent()).isEqualTo("첫 번째 댓글");
//
//        // 익명 건의 게시글
//        Board suggestion = boardRepository.save(Board.builder()
//                .boardType(BoardType.SUGGESTION)
//                .title("익명 댓글 테스트")
//                .content("익명 건의 게시글 내용")
//                .authorName("익명")
//                .authorId("5031003")
//                .build());
//
//        Comment comment2 = boardService.addComment(suggestion.getBoardNo(), "익명 댓글입니다.", normalUser);
//        assertThat(comment2.getAuthorName()).isEqualTo("익명");
//
//        // 공지사항 게시글 (예외 발생)
//        Board notice = boardRepository.save(Board.builder()
//                .boardType(BoardType.NOTICE)
//                .title("댓글 금지 테스트")
//                .content("공지사항입니다.")
//                .authorName("관리자")
//                .authorId("ceo")
//                .build());
//
//        assertThatThrownBy(() ->
//                boardService.addComment(notice.getBoardNo(), "이건 달리면 안 돼요", normalUser)
//        ).isInstanceOf(VerificationFailedException.class);
//
//        log.info("💬 댓글 등록 테스트 완료 (익명/공지사항 예외 포함)");
//    }
//
//    /* ✅ 6️⃣ 게시판 유형별 필터링 테스트 */
//    @Test
//    @Order(6)
//    @DisplayName("게시판 유형별 조회")
//    void getBoardsByTypeTest() {
//        boardRepository.save(Board.builder()
//                .boardType(BoardType.GENERAL)
//                .title("일반1")
//                .content("테스트1")
//                .authorName("홍길동")
//                .authorId("5032001")
//                .build());
//
//        boardRepository.save(Board.builder()
//                .boardType(BoardType.NOTICE)
//                .title("공지1")
//                .content("공지사항 내용")
//                .authorName("관리자")
//                .authorId("ceo")
//                .build());
//
//        List<BoardDto> generalBoards = boardService.getBoardsByType(BoardType.GENERAL);
//        List<BoardDto> noticeBoards = boardService.getBoardsByType(BoardType.NOTICE);
//
//        assertThat(generalBoards).allMatch(b -> b.getBoardType() == BoardType.GENERAL);
//        assertThat(noticeBoards).allMatch(b -> b.getBoardType() == BoardType.NOTICE);
//
//        log.info("📋 유형별 조회 완료: 일반={}건 / 공지사항={}건",
//                generalBoards.size(), noticeBoards.size());
//    }
//}
