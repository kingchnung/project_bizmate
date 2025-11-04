package com.bizmate.project.dto.projectmember;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ProjectMemberRequest {

    private Long projectId;     // 소속 프로젝트
    private Long empId;    // 추가할 직원 ID (Employee)
    private String projectRole; // 담당 역할 (예: 백엔드, 디자이너, PM)
}
