package com.bizmate.project.repository;

import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.ProjectTask;
import com.bizmate.project.domain.enums.task.TaskPriority;
import com.bizmate.project.domain.enums.task.TaskStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    // 1. 특정 프로젝트(Project)에 속한 모든 Task 조회
    List<ProjectTask> findByProject(Project project);

    // 2. 특정 담당자(ProjectMember)에게 할당된 모든 Task 조회
    // (지난번에 제안드린 대로 Task의 assignee를 ProjectMember로 변경했다고 가정)
    List<ProjectTask> findByAssignee(ProjectMember assignee);

    // 3. 특정 프로젝트에 속하면서 특정 상태(Status)인 Task만 조회
    List<ProjectTask> findByProjectAndStatus(Project project, TaskStatus status);

    // 우선순위(Priority) 기반 조회
    List<ProjectTask> findByProjectAndPriority(Project project, TaskPriority priority);

    //진행률(ProgressRate) 기반 조회
    List<ProjectTask> findByProgressRateLessThan(int rate);
    List<ProjectTask> findByProgressRateBetween(int start, int end);

    //기간 기반 조회 (예: 이번 주 마감)
    List<ProjectTask> findByEndDateBefore(LocalDate date);
    List<ProjectTask> findByStartDateBetween(LocalDate start, LocalDate end);

    //상태 + 담당자 복합 필터
    List<ProjectTask> findByAssigneeAndStatus(ProjectMember assignee, TaskStatus status);

    //정렬 조건용 (Spring Data JPA의 Sort 활용)
    List<ProjectTask> findByProject(Project project, Sort sort);

    //선택적 NativeQuery 예시 (대시보드 통계용)
    @Query("SELECT t.status, COUNT(t) FROM ProjectTask t WHERE t.project = :project GROUP BY t.status")
    List<Object[]> countTasksByStatus(@Param("project") Project project);

}