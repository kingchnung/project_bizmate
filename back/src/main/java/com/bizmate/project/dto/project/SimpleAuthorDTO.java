// com.bizmate.project.dto.project.SimpleAuthorDTO
package com.bizmate.project.dto.project;

import com.bizmate.hr.domain.UserEntity;
import lombok.Getter;

@Getter
public class SimpleAuthorDTO {
    private final Long userId;
    private final Long empId;
    private final String username;    // 로그인ID
    private final String empName;     // 직원명 (프론트에서 이걸 렌더링)
    private final String email;
    private final String positionName;
    private final String deptName;
    private final String deptCode;

    public SimpleAuthorDTO(UserEntity u) {
        this.userId = u.getUserId();
        this.empId = (u.getEmployee() != null) ? u.getEmployee().getEmpId() : null;
        this.username = u.getUsername();

        // 우선순위: UserEntity에 복제된 empName → 없으면 Employee.empName
        String nameFromUser = u.getEmpName();
        String nameFromEmployee = (u.getEmployee() != null) ? u.getEmployee().getEmpName() : null;
        this.empName = (nameFromUser != null && !nameFromUser.isBlank()) ? nameFromUser : nameFromEmployee;

        this.email = u.getEmail();
        this.positionName = u.getPositionName();
        this.deptName = u.getDeptName();
        this.deptCode = u.getDeptCode();
    }
}