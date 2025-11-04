package com.bizmate.project.dto.projectmember;


import com.bizmate.hr.domain.Employee;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberDTO {

    private Long projectMemberId;
    private Long projectId;
    private Long empId;
    private String empName;
    private Long deptId;
    private String deptName;
    private Long positionId;
    private String positionName;


    private String projectRole;


    @JsonCreator
    public ProjectMemberDTO(String empNo) {
        try {
            this.empId = Long.parseLong(empNo);
        } catch (NumberFormatException e) {
            this.empId = null;
        }
    }

    // ✅ Entity → DTO
    public ProjectMemberDTO(ProjectMember pm) {
        this.projectMemberId = pm.getProjectMemberId();
        this.projectRole = pm.getProjectRole();
        // status 필드가 엔티티에 없으면 주석 처리/삭제

        if (pm.getEmployee() != null) {
            this.empId = pm.getEmployee().getEmpId();
            this.empName = pm.getEmployee().getEmpName();
            this.deptId = pm.getEmployee().getDepartment().getDeptId();
            this.deptName = pm.getEmployee().getDepartment().getDeptName();
            this.positionId = pm.getEmployee().getPosition().getPositionCode();
            this.positionName = pm.getEmployee().getPosition().getPositionName();

        }
    }

    // ✅ DTO → Entity
    public ProjectMember toEntity(Project project, Employee employee) {
        return ProjectMember.builder()
                .projectMemberId(this.projectMemberId)
                .project(project)
                .employee(employee)
                .projectRole(this.projectRole)
                .build();
    }

    public static ProjectMemberDTO fromEntity(ProjectMember pm) {
        if (pm == null) return null;

        ProjectMemberDTO dto = new ProjectMemberDTO();
        dto.setProjectMemberId(pm.getProjectMemberId());
        dto.setProjectRole(pm.getProjectRole());

        // 🔹 Project 정보 (필요 시)
        if (pm.getProject() != null) {
            dto.setProjectId(pm.getProject().getProjectId());
        }

        // 🔹 Employee 정보
        if (pm.getEmployee() != null) {
            dto.setEmpId(pm.getEmployee().getEmpId());
            dto.setEmpName(pm.getEmployee().getEmpName());

            // 부서 정보
            if (pm.getEmployee().getDepartment() != null) {
                dto.setDeptId(pm.getEmployee().getDepartment().getDeptId());
                dto.setDeptName(pm.getEmployee().getDepartment().getDeptName());
            }

            // 직위 정보
            if (pm.getEmployee().getPosition() != null) {
                dto.setPositionId(pm.getEmployee().getPosition().getPositionCode());
                dto.setPositionName(pm.getEmployee().getPosition().getPositionName());
            }
        }

        return dto;
    }

}
