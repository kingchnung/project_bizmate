// com.bizmate.project.dto.project.SimpleDepartmentDTO
package com.bizmate.project.dto.project;

import com.bizmate.hr.domain.Department;
import lombok.Getter;

@Getter
public class SimpleDepartmentDTO {
    private final Long deptId;
    private final String deptCode;
    private final String deptName;
    private final Long managerId;

    public SimpleDepartmentDTO(Department d) {
        this.deptId = d.getDeptId();
        this.deptCode = d.getDeptCode();
        this.deptName = d.getDeptName();
        this.managerId = (d.getManager() != null) ? d.getManager().getEmpId() : null;
    }
}