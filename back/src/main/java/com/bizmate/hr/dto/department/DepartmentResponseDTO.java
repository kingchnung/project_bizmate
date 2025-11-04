package com.bizmate.hr.dto.department;

import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import lombok.Builder;
import lombok.Getter;

/**
 * [부서 조회 응답 DTO]
 */
@Getter
@Builder
public class DepartmentResponseDTO {

    private final Long deptId;
    private final String deptName;
    private final String deptCode;

    private final Long parentDeptId;
    private final String parentDeptName; // 상위 부서명 (조회 편의성)

    private final String isUsed;
    private final Long managerId;

    /**
     * Entity -> DTO 변환 메서드
     */
    public static DepartmentResponseDTO fromEntity(Department department) {

        String parentName = department.getParentDept() != null
                ? department.getParentDept().getDeptName()
                : null;
        Long parentId = department.getParentDept() != null
                ? department.getParentDept().getDeptId()
                : null;
        Employee manager = department.getManager() != null
                ? department.getManager()
                : null;

        return DepartmentResponseDTO.builder()
                .deptId(department.getDeptId())
                .deptName(department.getDeptName())
                .deptCode(department.getDeptCode())
                .parentDeptId(parentId)
                .parentDeptName(parentName)
                .isUsed(department.getIsUsed())
                .managerId(department.getManager() == null ? null : department.getManager().getEmpId())
                .build();
    }
}