package com.bizmate.hr.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📊 직원 통계용 DTO
 * - 나이대별 / 직급별 인원 통계에 공용 사용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatisticDTO {
    private String label; // ex) "20대", "과장"
    private Long count;   // 인원 수
}
