package com.bizmate.project.dto.task;

import com.bizmate.project.domain.enums.task.TaskPriority;
import com.bizmate.project.domain.enums.task.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 기존 프로젝트에 신규 Task를 추가하거나, 기존 Task를 수정할 때
 * 요청 데이터를 담는 DTO 입니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProjectTaskRequest {

    private String taskName;
    private String taskDescription;
    private LocalDate startDate;
    private LocalDate endDate;

    private TaskPriority taskPriority = TaskPriority.MEDIUM; // 기본값
    private TaskStatus taskStatus = TaskStatus.PLANNED;

    /**
     * 해당 업무를 담당할 담당자의 'ProjectMember ID' 입니다.
     * Employee ID가 아닌 ProjectMember ID를 사용하여,
     * 해당 프로젝트의 멤버에게만 업무를 할당할 수 있도록 강제합니다.
     */
    private Long assigneeMemberId;
    private Long projectId;
}
