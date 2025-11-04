package com.bizmate.project.dto.task;

import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.ProjectTask;
import com.bizmate.project.domain.enums.task.TaskPriority;
import com.bizmate.project.domain.enums.task.TaskStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskDTO {

    private Long taskId;
    private String taskName;
    private String taskDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private int progressRate;

    private Long assignee;

    private TaskPriority taskPriority; // ← enum 직접 사용 (string 변환은 Response에서)
    private TaskStatus taskStatus;

    private Long projectId;  // ← 소속 프로젝트 식별자

    // ✅ Entity → DTO
    public static ProjectTaskDTO fromEntity(ProjectTask task) {
        return ProjectTaskDTO.builder()
                .taskId(task.getTaskId())
                .taskName(task.getTaskName())
                .taskDescription(task.getTaskDescription())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .progressRate(task.getProgressRate())
                .taskPriority(task.getPriority())
                .taskStatus(task.getStatus())
                .projectId(task.getProject() != null ? task.getProject().getProjectId() : null)
                .assignee(task.getAssignee() != null ? task.getAssignee().getProjectMemberId() : null)
                .build();
    }

    // ✅ DTO → Entity
    public ProjectTask toEntity(Project project, ProjectMember assignee) {
        return ProjectTask.builder()
                .taskId(this.taskId)
                .taskName(this.taskName)
                .taskDescription(this.taskDescription)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .progressRate(this.progressRate)
                .priority(this.taskPriority)
                .status(this.taskStatus)
                .project(project)
                .assignee(assignee)
                .build();
    }
}