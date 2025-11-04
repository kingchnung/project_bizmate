package com.bizmate.project.controller;

import com.bizmate.project.dto.task.ProjectTaskDTO;
import com.bizmate.project.dto.task.ProjectTaskRequest;
import com.bizmate.project.domain.enums.task.TaskStatus;
import com.bizmate.project.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * ✅ ProjectTaskController
 * - 프로젝트별 Task 등록, 조회, 수정, 삭제를 담당
 * - 모든 응답은 DTO를 사용하며 엔티티 직접 노출 없음
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService TaskService;

    // ✅ 1. 새로운 Task 생성
    @PostMapping
    public ResponseEntity<ProjectTaskDTO> createTask(@RequestBody ProjectTaskRequest request) {
        ProjectTaskDTO createdTask = TaskService.createTask(request);
        return ResponseEntity.ok(createdTask);
    }

    // ✅ 2. 단일 Task 조회
    @GetMapping("/{taskId}")
    public ResponseEntity<ProjectTaskDTO> getTaskById(@PathVariable Long taskId) {
        ProjectTaskDTO task = TaskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    // ✅ 3. 전체 Task 목록 조회
    @GetMapping
    public ResponseEntity<List<ProjectTaskDTO>> getAllTasks() {
        return ResponseEntity.ok(TaskService.getAllTasks());
    }

    // ✅ 4. 특정 프로젝트 내 모든 Task 조회
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ProjectTaskDTO>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(TaskService.getTasksByProjectId(projectId));
    }

    // ✅ 5. 특정 담당자(TaskMember) 기준 조회
    @GetMapping("/assignee/{assigneeMemberId}")
    public ResponseEntity<List<ProjectTaskDTO>> getTasksByAssignee(@PathVariable Long assigneeMemberId) {
        return ResponseEntity.ok(TaskService.getTasksByAssigneeId(assigneeMemberId));
    }

    // ✅ 6. 프로젝트 + 상태별 Task 조회
    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<List<ProjectTaskDTO>> getTasksByProjectAndStatus(
            @PathVariable Long projectId,
            @PathVariable TaskStatus status
    ) {
        return ResponseEntity.ok(TaskService.getTasksByProjectAndStatus(projectId, status));
    }

    // ✅ 7. Task 상태 변경
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ProjectTaskDTO> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status
    ) {
        return ResponseEntity.ok(TaskService.updateTaskStatus(taskId, status));
    }

    // ✅ 8. 진행률 업데이트
    @PatchMapping("/{taskId}/progress")
    public ResponseEntity<ProjectTaskDTO> updateTaskProgress(
            @PathVariable Long taskId,
            @RequestParam int progressRate
    ) {
        return ResponseEntity.ok(TaskService.updateTaskProgress(taskId, progressRate));
    }

    // ✅ 9. 특정 날짜 이전 마감 Task 조회
    @GetMapping("/due-before")
    public ResponseEntity<List<ProjectTaskDTO>> getTasksEndingBefore(
            @RequestParam LocalDate date
    ) {
        return ResponseEntity.ok(TaskService.getTasksEndingBefore(date));
    }

    // ✅ 10. Task 삭제
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        TaskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
