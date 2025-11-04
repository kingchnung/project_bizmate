package com.bizmate.groupware.approval.repository.document;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.document.DocumentStatus;
import com.bizmate.groupware.approval.domain.policy.ApproverStep;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 전자결재 문서 Repository
 * - Spring Data JPA 기반
 * - 기본 CRUD + 조건검색
 */
@Repository
public interface ApprovalDocumentsRepository extends JpaRepository<ApprovalDocuments, String> {


    /**
     * 상태별 문서 목록
     */
    Page<ApprovalDocuments> findByStatus(DocumentStatus status, Pageable pageable);


    /**
     * 날짜 범위 내 부서별 카운트 (문서번호 생성용)
     */
    long countByDepartment_DeptIdAndCreatedAtBetween(Long deptId,
                                                     LocalDateTime start,
                                                     LocalDateTime end);

    /**
     * 페이징 검색
     */
    Page<ApprovalDocuments> findAll(Pageable pageable);


    @Query("""
            SELECT d
            FROM ApprovalDocuments d
            LEFT JOIN d.authorEmployee e
            WHERE (:keyword IS NULL 
                   OR UPPER(FUNCTION('REPLACE', d.title, ' ', '')) LIKE 
                      UPPER(FUNCTION('REPLACE', CONCAT('%', :keyword, '%'), ' ', ''))
                   OR UPPER(FUNCTION('REPLACE', e.empName, ' ', '')) LIKE 
                      UPPER(FUNCTION('REPLACE', CONCAT('%', :keyword, '%'), ' ', '')))
            """)
    Page<ApprovalDocuments> searchDocuments(@Param("keyword") String keyword, Pageable pageable);


    @Query("""
            SELECT DISTINCT d
            FROM ApprovalDocuments d
            LEFT JOIN FETCH d.authorUser au
            LEFT JOIN FETCH au.employee e
            """)
    List<ApprovalDocuments> findAllWithAuthorAndEmployee();

    @Query("""
                SELECT d FROM ApprovalDocuments d
                LEFT JOIN FETCH d.department dept
                LEFT JOIN FETCH d.authorUser user
                LEFT JOIN FETCH user.employee emp
                WHERE d.docId = :docId
            """)
    Optional<ApprovalDocuments> findWithDetailsByDocId(@Param("docId") String docId);

}
