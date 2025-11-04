package com.bizmate.project.dto.projectmember;

import com.bizmate.project.domain.ProjectMember;
import lombok.Getter;

@Getter
public class ProjectMemberResponseDTO {
    private final Long projectMemberId;
    private final Long empId;
    private final String empName;
    private final String role;

    public ProjectMemberResponseDTO(ProjectMember member) {
        this.projectMemberId = member.getProjectMemberId();
        this.empId = member.getEmployee().getEmpId();
        this.empName = member.getEmployee().getEmpName();
        this.role = member.getProjectRole();
    }
}
