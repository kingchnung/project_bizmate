package com.bizmate.hr.dto.employee;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * [직원 정보 등록 요청 DTO]
 * - 관리자 페이지에서 신규 직원 등록 시 사용
 */
@Getter
@NoArgsConstructor
public class EmployeeCreateRequestDTO {

    private String empName;         // 직원명 (필수)
    private String gender;          // 성별
    private LocalDate birthDate;    // 생년월일
    private String phone;           // 전화번호 (필수)
    private String email;           // 이메일 (필수)
    private String address;         // 주소
    private String deptCode;
    private Long positionCode;        // 직위
    private Long gradeCode;           // 직급 (필수)
    private LocalDate startDate;    // 입사일 (필수)

    // *관리자가 직접 입력하지 않는 필드: 퇴사일, 상태, 경력연수, 주민번호 마스킹 등은 제외*
    // *직급/직위 코드 대신 실제 값을 사용한다면 데이터 타입 조정 필요*
}