package com.bizmate.project.service;

import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.ProjectTask;
import com.bizmate.project.domain.enums.task.TaskStatus;
import com.bizmate.project.dto.task.ProjectTaskDTO;
import com.bizmate.project.dto.task.ProjectTaskRequest;
import com.bizmate.project.repository.ProjectMemberRepository;
import com.bizmate.project.repository.ProjectRepository;
import com.bizmate.project.repository.ProjectTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl  implements TaskService{

    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;


    @Override
    public ProjectTaskDTO createTask(ProjectTaskRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다."));

        ProjectMember assignee = projectMemberRepository.findById(request.getAssigneeMemberId())
                .orElseThrow(() -> new IllegalArgumentException("담당자를 찾을 수 없습니다."));

        ProjectTask task = ProjectTask.builder()
                .taskName(request.getTaskName())
                .taskDescription(request.getTaskDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .priority(request.getTaskPriority())
                .status(request.getTaskStatus())
                .project(project)
                .assignee(assignee)
                .build();

        return ProjectTaskDTO.fromEntity(projectTaskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectTaskDTO getTaskById(Long taskId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Task를 찾을 수 없습니다."));
        return ProjectTaskDTO.fromEntity(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectTaskDTO> getAllTasks() {
        return projectTaskRepository.findAll()
                .stream()
                .map(ProjectTaskDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectTaskDTO> getTasksByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        return projectTaskRepository.findByProject(project)
                .stream()
                .map(ProjectTaskDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectTaskDTO> getTasksByAssigneeId(Long assigneeMemberId) {
        ProjectMember assignee = projectMemberRepository.findById(assigneeMemberId)
                .orElseThrow(() -> new IllegalArgumentException("담당자를 찾을 수 없습니다."));
        return projectTaskRepository.findByAssignee(assignee)
                .stream()
                .map(ProjectTaskDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectTaskDTO> getTasksByProjectAndStatus(Long projectId, TaskStatus status) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        return projectTaskRepository.findByProjectAndStatus(project, status)
                .stream()
                .map(ProjectTaskDTO::fromEntity)
                .toList();
    }

    @Override
    public ProjectTaskDTO updateTaskStatus(Long taskId, TaskStatus status) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task를 찾을 수 없습니다."));

        task.setStatus(status);

        if (status == TaskStatus.COMPLETED) {
            task.setProgressRate(100);
        } else if (status == TaskStatus.PLANNED) {
            task.setProgressRate(0);
        }

        return ProjectTaskDTO.fromEntity(projectTaskRepository.save(task));
    }

    @Override
    public ProjectTaskDTO updateTaskProgress(Long taskId, int progressRate) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task를 찾을 수 없습니다."));

        progressRate = Math.max(0, Math.min(progressRate, 100));
        task.setProgressRate(progressRate);

        if (progressRate == 100) {
            task.setStatus(TaskStatus.COMPLETED);
        }

        return ProjectTaskDTO.fromEntity(projectTaskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectTaskDTO> getTasksEndingBefore(LocalDate date) {
        return projectTaskRepository.findByEndDateBefore(date)
                .stream()
                .map(ProjectTaskDTO::fromEntity)
                .toList();
    }
    @Override
    public void deleteTask(Long taskId) {
        projectTaskRepository.deleteById(taskId);
    }
}
