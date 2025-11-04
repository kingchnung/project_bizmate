package com.bizmate.project.service;

import com.bizmate.project.domain.enums.task.TaskStatus;
import com.bizmate.project.dto.task.ProjectTaskDTO;
import com.bizmate.project.dto.task.ProjectTaskRequest;

import java.time.LocalDate;
import java.util.List;

public interface TaskService {
    // ✅ 1. 신규 Task 생성
    ProjectTaskDTO createTask(ProjectTaskRequest request);

    // ✅ 2. 단건 조회
    ProjectTaskDTO getTaskById(Long taskId);

    // ✅ 3. 전체 조회
    List<ProjectTaskDTO> getAllTasks();

    // ✅ 4. 프로젝트별 조회
    List<ProjectTaskDTO> getTasksByProjectId(Long projectId);

    // ✅ 5. 담당자별 조회
    List<ProjectTaskDTO> getTasksByAssigneeId(Long assigneeMemberId);

    // ✅ 6. 상태별 조회
    List<ProjectTaskDTO> getTasksByProjectAndStatus(Long projectId, TaskStatus status);

    // ✅ 7. 상태 변경
    ProjectTaskDTO updateTaskStatus(Long taskId, TaskStatus status);

    // ✅ 8. 진행률 업데이트
    ProjectTaskDTO updateTaskProgress(Long taskId, int progressRate);

    // ✅ 9. 마감일 이전 Task 조회
    List<ProjectTaskDTO> getTasksEndingBefore(LocalDate date);

    // ✅ 10. 삭제
    void deleteTask(Long taskId);
}
