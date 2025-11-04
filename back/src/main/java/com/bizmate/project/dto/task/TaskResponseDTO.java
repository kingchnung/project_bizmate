package com.bizmate.project.dto.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TaskResponseDTO {

    private Long taskId;
    private String taskName;
    private String taskDescription;

    private String taskPriority;
    private String taskStatus;

    private int progressRate;

    private LocalDate taskStartDate;
    private LocalDate taskEndDate;

    private String assigneeName; // 담당자 이름 (ProjectMember → Employee)
    private String projectName;  // 소속 프로젝트명
}
