package com.bizmate.groupware.board.service;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardType;
import com.bizmate.groupware.board.domain.Comment;
import com.bizmate.groupware.board.dto.BoardDto;
import com.bizmate.hr.security.UserPrincipal;

import java.util.List;

public interface BoardService {
    BoardDto createBoard(BoardDto dto, UserPrincipal user);

    void deleteBoard(Long boardId, UserPrincipal user);

    Comment addComment(Long boardId, String content, UserPrincipal user);

    List<BoardDto> getBoardsByType(BoardType type);

    BoardDto getBoard(Long id , UserPrincipal user);

    BoardDto updateBoard(Long id, BoardDto dto, UserPrincipal user);

    PageResponseDTO<BoardDto> getBoardPage(PageRequestDTO pageRequestDTO, UserPrincipal user);

    PageResponseDTO<BoardDto> getBoardPageForAdmin(PageRequestDTO pageRequestDTO);

}
