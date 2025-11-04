package com.bizmate.hr.dto.employee;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * [직원 정보 수정 요청 DTO]
 * - 관리자 (emp_admin_001) 및 일반 직원 (emp_user_001) 수정 요청 시 공통으로 사용
 */
@Getter
@NoArgsConstructor
public class EmployeeUpdateRequestDTO {

    // 1. 일반 직원 수정 가능 항목 (emp_user_001)

    private String phone;           // 전화번호
    private String email;           // 이메일
    private String address;         // 주소

    // 2. 관리자 수정 가능 항목 (emp_admin_001 - 부서, 직급, 직위 등은 인사발령 시점에 변경됨)
    // *인사카드 수정 요구사항에 명시된 항목: 전화번호, 주소, 부서ID, 퇴사일, 근속연수, 직급, 직위, 상태*

    // **[핵심] 부서/직급/직위 변경은 인사발령(emp_admin_002) 로직을 따르므로 이 DTO에서 제거**
    // **[단축] 퇴사일/상태/경력연수 등 인사카드 단독 수정 항목**
    private String empNo;
    private String empName;
    private LocalDate startDate;
    private LocalDate leaveDate;    // 퇴사일
    private String status;          // 재직상태
    private String deptCode;
    private Long positionCode;
    private Long gradeCode;
    private Float careerYears;      // 근속연수 (경력)

}