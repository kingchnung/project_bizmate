package com.bizmate.hr.controller;

import com.bizmate.hr.dto.employee.EmployeeStatisticDTO;
import com.bizmate.hr.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees/statistics")
@RequiredArgsConstructor
public class EmployeeStatisticsController {

    private final EmployeeService employeeService;

    /**
     * 📊 나이대별 인원 통계
     */
    @GetMapping("/age")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<EmployeeStatisticDTO>> getAgeStatistics() {
        return ResponseEntity.ok(employeeService.getAgeStatistics());
    }

    /**
     * 🎖️ 직급별 인원 통계
     */
    @GetMapping("/grade")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<EmployeeStatisticDTO>> getGradeStatistics() {
        return ResponseEntity.ok(employeeService.getGradeStatistics());
    }
}
