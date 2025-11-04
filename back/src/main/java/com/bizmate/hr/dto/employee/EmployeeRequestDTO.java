package com.bizmate.hr.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate; // 입사일 관리를 위해 LocalDate import

/**
 * [EmployeeRequestDTO]
 * - 직원 정보 등록/수정 요청(Request)용 DTO (NOT NULL 항목 모두 포함)
 */
@Data
public class EmployeeRequestDTO {

    // 1. 기본 식별 정보
    //@NotBlank(message = "사원 번호는 필수 항목입니다.")
    //@Schema(description = "사원번호 (수정 시에만 사용, 신규 등록시 자동 생성)")
    private String empNo;

    @NotBlank(message = "이름은 필수 항목입니다.")
    private String empName;

    @NotBlank(message = "전화번호는 필수 항목입니다.")
    private String phone; // ★ 추가됨 (NOT NULL)

    @Email(message = "유효한 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    private String email; // ★ 추가됨 (NOT NULL)

    // 2. 조직 및 직급/직책 정보 (FK 식별자)
    @NotNull(message = "부서 Code는 필수 항목입니다.")
    private String deptCode;

    @NotNull(message = "직책 코드는 필수 항목입니다.")
    private Long positionCode; // ★ 수정됨: Position 객체 대신 Long 타입의 FK (NOT NULL)

    @NotNull(message = "직급 코드는 필수 항목입니다.")
    private Long gradeCode; // ★ 추가됨: Grade 객체 대신 Long 타입의 FK (NOT NULL)

    // 3. 고용 상태 및 날짜 정보
    @NotNull(message = "입사일은 필수 항목입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 날짜 형식 지정
    private LocalDate startDate; // ★ 추가됨 (NOT NULL)

    @NotBlank(message = "상태는 필수 항목입니다.")
    @Pattern(regexp = "ACTIVE|RETIRED|BREAK", message="상태는 ACTIVE/RETIRED/BREAK 만 허용")
    private String status;
     // 'active', 'retire', 'break' 중 하나

    // 참고: empId (PK)는 등록 시에는 필요 없고 수정 시에만 PathVariable로 사용됩니다.
}