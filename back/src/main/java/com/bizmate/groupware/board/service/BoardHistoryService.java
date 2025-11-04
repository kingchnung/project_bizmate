package com.bizmate.groupware.board.service;

import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardHistory;
import com.bizmate.groupware.board.repository.BoardHistoryRepository;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardHistoryService {

    private final BoardHistoryRepository boardHistoryRepository;

    @Transactional
    public void saveHistory(Board board, UserPrincipal user, String actionType, String comment) {
        BoardHistory history = BoardHistory.builder()
                .boardNo(board.getBoardNo())
                .actorUserId(user.getUsername())
                .actionType(actionType)
                .actionComment(comment)
                .actionTimestamp(LocalDateTime.now())
                .build();

        boardHistoryRepository.save(history);

        log.info("🧾 [BoardHistory] {} | boardNo={} | by={} | at={}",
                actionType, board.getBoardNo(), user.getUsername(), history.getActionTimestamp());
    }

    public List<BoardHistory> getHistory(Long boardNo) {
        return boardHistoryRepository.findByBoardNoOrderByActionTimestampDesc(boardNo);
    }
}
