package com.bizmate.hr.dto.department;

import com.bizmate.hr.domain.Department;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentDTO {
    private Long deptId;
    private String deptName;
    private String deptCode;
    private Long managerId;


    public static DepartmentDTO fromEntity(Department dept) {
        return DepartmentDTO.builder()
                .deptId(dept.getDeptId())
                .deptName(dept.getDeptName())
                .deptCode(dept.getDeptCode())
                .managerId(dept.getManager().getEmpId())
                .build();
    }
}