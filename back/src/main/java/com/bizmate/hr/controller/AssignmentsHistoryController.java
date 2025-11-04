package com.bizmate.hr.controller;

import com.bizmate.hr.dto.assignment.AssignmentHistoryDTO;
import com.bizmate.hr.dto.assignment.AssignmentHistoryRequestDTO;
import com.bizmate.hr.service.AssignmentsHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Slf4j
public class AssignmentsHistoryController {

    private final AssignmentsHistoryService assignmentsHistoryService;

    /**
     * 📌 [부서이동 / 인사이동 등록]
     * 접근권한: ROLE_MANAGER 이상 (ROLE_CEO, ROLE_ADMIN 포함)
     * 기능: 지정된 직원의 부서/직급/직위 변경 및 이력 저장
     */
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_CEO','ROLE_ADMIN')")
    @PostMapping("/move")
    public AssignmentHistoryDTO moveAssignment(
            @RequestBody AssignmentHistoryRequestDTO dto,
            Authentication authentication
    ) {
        String username = "admin";
        log.info("👤 [{}] 부서이동 요청: {}", username, dto);

        AssignmentHistoryDTO result = assignmentsHistoryService.createAssignment(dto, username);

        return result;
    }

    /**
     * 📌 [직원별 발령이력 조회]
     * 접근권한: ROLE_EMPLOYEE 이상 (모든 로그인 사용자)
     * 기능: 특정 직원의 부서/직위 이동 이력 조회
     */
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_MANAGER','ROLE_CEO','ROLE_ADMIN')")
    @GetMapping("/employee/{empId}")
    public List<AssignmentHistoryDTO> getHistoryByEmployee(@PathVariable Long empId) {
        log.info("📋 직원 발령이력 조회 요청 - empId: {}", empId);
        return assignmentsHistoryService.getHistoryByEmployee(empId);
    }

    /**
     * 📌 [부서별 발령이력 조회]
     * 접근권한: ROLE_MANAGER 이상 (ROLE_CEO, ROLE_ADMIN 포함)
     * 기능: 특정 부서의 이동 내역을 전체 조회
     */
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_CEO','ROLE_ADMIN')")
    @GetMapping("/department/{deptId}")
    public List<AssignmentHistoryDTO> getHistoryByDepartment(@PathVariable Long deptId) {
        log.info("📋 부서별 발령이력 조회 요청 - deptId: {}", deptId);
        return assignmentsHistoryService.getHistoryByDepartment(deptId);
    }
}
