package com.bizmate.groupware.board.repository;

import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b WHERE b.isDeleted = false ORDER BY " +
            "CASE WHEN b.boardType = 'NOTICE' THEN 0 ELSE 1 END, b.boardNo DESC")
    List<Board> findAllOrderByPriority();

    List<Board> findByBoardTypeAndIsDeletedFalse(BoardType boardType);

    @Query("SELECT b FROM Board b WHERE b.isDeleted = false")
    List<Board> findAllActive();

    @Query("""
            SELECT b
            FROM Board b
            WHERE b.isDeleted = false
              AND (:boardType IS NULL OR b.boardType = :boardType)
              AND (
                   :keyword IS NULL OR :keyword = ''
                   OR (
                        ( UPPER(:searchType) = 'TITLE'
                          AND LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) )
                     OR ( UPPER(:searchType) = 'CONTENT'
                          AND LOWER(CAST(b.content AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) )
                     OR ( UPPER(:searchType) IN ('AUTHOR','AUTHORNAME')
                          AND LOWER(b.authorName) LIKE LOWER(CONCAT('%', :keyword, '%')) )
                     OR ( ( :searchType IS NULL OR :searchType = '' OR UPPER(:searchType) = 'ALL' )
                          AND (
                                LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                             OR LOWER(CAST(b.content AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
                             OR LOWER(b.authorName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                          )
                        )
                   )
              )
            ORDER BY 
              CASE WHEN b.boardType = com.bizmate.groupware.board.domain.BoardType.NOTICE THEN 0 ELSE 1 END,
              b.createdAt DESC
            """)
    Page<Board> findActiveByKeyword(
            @Param("keyword") String keyword,
            @Param("boardType") BoardType boardType,
            @Param("searchType") String searchType,
            Pageable pageable
    );

    @Query("SELECT b FROM Board b WHERE (:keyword IS NULL OR b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<Board> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
