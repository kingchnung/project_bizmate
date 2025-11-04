package com.bizmate.hr.dto.department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * [DepartmentOverviewDTO]
 * 부서 현황 조회용 DTO
 * - 인원 수, 평균 나이, 평균 근속연수 등 통계 정보 포함
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentOverviewDTO {

    private Long deptId;          // 부서 ID
    private String deptName;      // 부서명
    private String deptCode;
    private Long parentDeptId;
    private int employeeCount;    // 소속 인원 수
    private double avgAge;        // 평균 나이
    private double avgYears;      // 평균 근속연수

    private Map<String, Long> gradeCount;// 필요하다면 직급별 통계도 나중에 추가 가능

}
