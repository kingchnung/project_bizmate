package com.bizmate.hr.controller;

import com.bizmate.hr.dto.department.*;
import com.bizmate.hr.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 📊 부서현황 조회 (전체 직원 접근 가능)
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/overview")
    public List<DepartmentOverviewDTO> getDepartmentOverview() {
        log.info("📊 부서 현황 조회 요청");
        return departmentService.getDepartmentOverview();
    }

    /**
     * 📋 전체 부서 목록 조회
     */
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CEO')")
    @GetMapping
    public List<DepartmentResponseDTO> getAllDepartments() {
        log.info("📋 전체 부서 목록 조회");
        return departmentService.getAllDepartments();
    }

    /**
     * 📋 부서 상세조회
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/{deptId}")
    public DepartmentResponseDTO getDepartmentDetail(@PathVariable Long deptId) {
        log.info("📋 부서 상세조회 요청: {}", deptId);
        return departmentService.getDepartmentDetail(deptId);
    }

    /**
     * 🏗️ 부서 생성 (관리자 전용)
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public DepartmentResponseDTO createDepartment(@RequestBody DepartmentCreateRequestDTO dto) {
        log.info("🏗️ 부서 생성 요청: {}", dto);
        return departmentService.createDepartment(dto);
    }

    /**
     * ✏️ 부서 수정 (관리자 전용)
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{deptId}")
    public DepartmentResponseDTO updateDepartment(
            @PathVariable Long deptId,
            @RequestBody DepartmentUpdateRequestDTO dto
    ) {
        log.info("✏️ 부서 수정 요청: deptId={}, dto={}", deptId, dto);
        return departmentService.updateDepartment(deptId, dto);
    }

    @PutMapping("/{deptId}/manager")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CEO')")
    public ResponseEntity<?> assignManager(
            @PathVariable Long deptId,
            @RequestBody DepartmentManagerDTO dto) {

        DepartmentDTO updatedDept = departmentService.assignManager(deptId, dto.getManagerId());
        return ResponseEntity.ok(Map.of(
                "message", "부서장이 성공적으로 임명되었습니다.",
                "departmentId", deptId,
                "managerId", dto.getManagerId()

        ));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{deptId}")
    public void deactivateDepartment(@PathVariable Long deptId) {
        log.info("🟡 부서 비활성화 요청: {}", deptId);
        departmentService.deleteDepartment(deptId); // 이제 이 서비스는 비활성화를 수행합니다.
    }

    /**
     * 🗑️🔥 부서 영구 삭제 (최고 관리자 전용)
     * 위험한 기능이므로 완전히 다른 경로로 분리합니다.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')") // 또는 더 강력한 권한으로 제한
    @DeleteMapping("/{deptId}/permanent")
    public void permanentlyDeleteDepartment(@PathVariable Long deptId) {
        log.warn("🗑️🔥 부서 영구 삭제 요청: {}", deptId);
        departmentService.permanentlyDeleteDepartment(deptId);
    }
}
