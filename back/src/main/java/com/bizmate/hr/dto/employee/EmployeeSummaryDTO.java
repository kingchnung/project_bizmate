package com.bizmate.hr.dto.employee;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor

@Builder
public class EmployeeSummaryDTO {

    private Long empId;       // 직원 ID
    private String empNo;     // 사번
    private String empName;   // 이름
    private String gradeName; // 직급명
    private String positionName;
    private String phone;     // 전화번호
    private String email;     // 이메일
    private String deptName;  // 부서명

    public EmployeeSummaryDTO(Long empId, String empNo, String empName,
                              String gradeName, String positionName,
                              String phone, String email, String deptName) {
        this.empId = empId;
        this.empNo = empNo;
        this.empName = empName;
        this.gradeName = gradeName;
        this.positionName = positionName;
        this.phone = phone;
        this.email = email;
        this.deptName = deptName;
    }
}
