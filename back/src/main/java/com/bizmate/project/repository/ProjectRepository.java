package com.bizmate.project.repository;

import com.bizmate.hr.domain.Department;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.enums.project.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // ✅ 1. 특정 부서가 담당하는 모든 프로젝트 조회
    List<Project> findByDepartment(Department department);

    // ✅ 2. 특정 상태(PLANNING, IN_PROGRESS 등)의 프로젝트 조회
    List<Project> findByStatus(ProjectStatus status);

    // ✅ 3. 프로젝트명 키워드 검색 (대소문자 무시)
    List<Project> findByProjectNameContainingIgnoreCase(String keyword);

    // ✅ 4. 특정 기간 내 시작된 프로젝트 조회
    List<Project> findByStartDateBetween(LocalDate start, LocalDate end);

    // ✅ 5. 특정 기간 내 종료 예정인 프로젝트 조회
    List<Project> findByEndDateBefore(LocalDate endDate);

    // ✅ 논리삭제(종료일 갱신)용 쿼리
    @Modifying
    @Transactional
    @Query("UPDATE Project p SET p.endDate = :endDate WHERE p.projectId = :projectId")
    int updateEndDate(@Param("projectId") Long projectId, @Param("endDate") LocalDate endDate);

    // ✅ 관리자용 전체 조회
    @Query("SELECT p FROM Project p ORDER BY p.startDate DESC")
    List<Project> findAllForAdmin();

    // ✅ 일반 사용자용: 종료되지 않은 프로젝트만 조회
    @Query("""
        SELECT p FROM Project p
        WHERE (p.endDate IS NULL OR p.endDate >= CURRENT_DATE)
        ORDER BY p.startDate DESC
    """)
    List<Project> findActiveProjects();

    @Query("""
    SELECT p FROM Project p
    LEFT JOIN FETCH p.participants pm
    LEFT JOIN FETCH pm.employee
    WHERE p.projectId = :id
    """)
    Optional<Project> findByIdWithMembers(@Param("id") Long id);


}