package com.bizmate.hr.dto.assignment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * [인사발령 등록 요청 DTO]
 * - 관리자 페이지에서 인사발령 등록 시 사용
 */
@Getter
@NoArgsConstructor
public class AssignmentHistoryRequestDTO {

    @NotNull(message = "직원 ID는 필수입니다.")
    private Long empId;                 // 발령 대상 직원 ID (EMPLOYEES의 FK)

    @NotNull(message = "발령일자는 필수입니다.")
    private LocalDate assDate;          // 발령일자 (발령이 유효해지는 날)

//    private Long prevDeptId;
//    private Long prevPositionCode;
//    private Long prevGradeCode;

    @NotNull(message = "신규 부서 ID는 필수입니다.")
    private Long newDeptId;             // 신규 부서 ID (DEPARTMENTS의 FK)

    @NotNull(message = "신규 직책 코드는 필수입니다.")
    private Long newPositionCode;       // 신규 직책 코드 (POSITIONS의 FK)

    @NotNull(message = "신규 직급 코드는 필수입니다.")
    private Long newGradeCode;          // 신규 직급 코드 (GRADE의 FK)

    @NotBlank(message = "발령 사유는 필수입니다.")
    @Size(max = 1000, message = "발령 사유는 1000자 이하로 입력해야 합니다.")
    private String reason;              // 발령 사유

    // 이전 정보는 DTO에 포함하지 않습니다.
    // 서버(Service 계층)에서 empId를 통해 현재 Employee 엔티티를 조회하여
    // '이전 부서/직책/직급' 정보를 가져와야 데이터 정합성이 보장됩니다.

    // 등록자 ID (creBy)는 Spring Security의 인증 정보에서 추출하여 Service에서 자동 주입합니다.
}