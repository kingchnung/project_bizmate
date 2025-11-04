package com.bizmate.hr.dto.assignment;

import com.bizmate.hr.domain.AssignmentsHistory;

import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.domain.code.Grade;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [인사발령 이력 조회 응답 DTO]
 * - 이력 목록 및 상세 정보를 프론트엔드로 전송할 때 사용
 */
@Getter
@Builder
public class AssignmentHistoryDTO {

    private final Long assId;
    private final Long empId;
    private final String empName;          // 직원 이름
    private final LocalDate assDate;       // 발령일자

    // --- 변경 전 정보 ---
    private final Long prevDeptId;
    private final String previousDepartmentName;
    private final Long prevPositionCode;
    private final String previousPositionName;
    private final Long prevGradeCode;
    private final String previousGradeName;

    // --- 변경 후 정보 ---
    private final Long newDeptId;
    private final String newDepartmentName;
    private final Long newPositionCode;
    private final String newPositionName;
    private final Long newGradeCode;
    private final String newGradeName;

    private final String reason;                 // 발령 사유

    private final String createdBy;              // 등록자 이름
    private final LocalDateTime creDate;         // 등록일

    /**
     * Entity -> DTO 변환 메서드
     */
    public static AssignmentHistoryDTO fromEntity(AssignmentsHistory history) {

        // 직원 정보는 필수 체크
        if (history.getEmployee() == null) {
            throw new IllegalArgumentException("발령 이력에 연결된 직원 정보가 누락되었습니다.");
        }

        // 등록자 이름 추출 (User 엔티티의 empName 필드 사용)
        String createdByName = history.getCreatedBy() != null ? history.getCreatedBy().getEmpName() : "시스템";

        return AssignmentHistoryDTO.builder()
                .assId(history.getAssId())
                .empId(history.getEmployee().getEmpId())
                .empName(history.getEmployee().getEmpName())
                .assDate(history.getAssDate())

                // 이전 정보
                .prevDeptId(getDeptId(history.getPreviousDepartment()))
                .previousDepartmentName(getDeptName(history.getPreviousDepartment()))
                .prevPositionCode(getPositionCode(history.getPreviousPosition()))
                .previousPositionName(getPositionName(history.getPreviousPosition()))
                .prevGradeCode(getGradeCode(history.getPreviousGrade()))
                .previousGradeName(getGradeName(history.getPreviousGrade()))

                // 신규 정보
                .newDeptId(history.getNewDepartment().getDeptId())
                .newDepartmentName(getDeptName(history.getNewDepartment()))
                .newPositionCode(history.getNewPosition().getPositionCode())
                .newPositionName(getPositionName(history.getNewPosition()))
                .newGradeCode(history.getNewGrade().getGradeCode())
                .newGradeName(getGradeName(history.getNewGrade()))

                .reason(history.getReason())
                .createdBy(createdByName)
                .creDate(history.getCreDate())
                .build();
    }

    // --- Private Static 헬퍼 메서드 영역 ---

    /** 부서명 추출 (null 체크 포함) */
    private static String getDeptName(Department dept) {
        return dept != null ? dept.getDeptName() : "미지정";
    }

    /** 직책명 추출 (null 체크 포함) */
    private static String getPositionName(Position pos) {
        return pos != null ? pos.getPositionName() : "미지정";
    }

    /** 직급명 추출 (null 체크 포함) */
    private static String getGradeName(Grade grade) {
        return grade != null ? grade.getGradeName() : "미지정";
    }

    /** 부서 ID 추출 (null 체크 포함) */
    private static Long getDeptId(Department dept) {
        return dept != null ? dept.getDeptId() : null;
    }

    /** 직책 코드 추출 (null 체크 포함) */
    private static Long getPositionCode(Position pos) {
        return pos != null ? pos.getPositionCode() : null;
    }

    /** 직급 코드 추출 (null 체크 포함) */
    private static Long getGradeCode(Grade grade) {
        return grade != null ? grade.getGradeCode() : null;
    }
}