package com.bizmate.hr.controller;

import com.bizmate.hr.dto.employee.*;
import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.hr.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("isAuthenticated()") // 진입 보장
    public List<EmployeeDTO> getAllEmployees(Authentication authentication) {
        var principal = (UserPrincipal) authentication.getPrincipal();
        boolean admin = principal.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("sys:admin")
                        || a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_CEO")
                        || a.getAuthority().equals("data:read:all")
        );

        return admin ? employeeService.getAllEmployees()
                : employeeService.getActiveEmployees();
    }


    @GetMapping("/summary")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<EmployeeSummaryDTO> getEmployeeSummaries() {
        return employeeService.getEmployeeSummaries();
    }

    @GetMapping("/{empId}/detail")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<EmployeeDetailDTO> getEmployeeDetail(@PathVariable Long empId){
        EmployeeDetailDTO dto = employeeService.getEmployeeDetail(empId);
        return ResponseEntity.ok(dto);
    }

    // ★ 권한 설정: 'emp:create' 권한이 있는 사용자만 접근 가능
    @PostMapping("/add")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmployeeDTO> createEmployee(
            @RequestBody @Valid EmployeeCreateRequestDTO requestDTO){
        EmployeeDTO created = employeeService.createEmployee(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    @GetMapping("/next-no/{deptCode}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, String>> getNextEmpNoByDept(@PathVariable String deptCode){
        String EmpNo = employeeService.generateEmpNo(deptCode);
        return ResponseEntity.ok(Map.of("EmpNo", EmpNo));
    }


    @PutMapping("/{empId}")
    @PreAuthorize("hasRole('ROLE_CEO') or hasRole('ROLE_MANAGER') or (#empId == authentication.name)")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable Long empId,
            @RequestBody @Valid EmployeeUpdateRequestDTO requestDTO){
        // ★ 변경: RequestDTO를 받고 DTO를 반환
        EmployeeDTO updated = employeeService.updateEmployee(empId, requestDTO);
        return ResponseEntity.ok(updated);
    }

    //관리자용 해당직원의 퇴직처리
    @PutMapping("/{empId}/retire")
    @PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_MANAGER')")
    public ResponseEntity<EmployeeDTO> retireEmployee(@PathVariable Long empId) {
        EmployeeDTO retired = employeeService.retireEmployee(empId);
        return ResponseEntity.ok(retired);
    }

    // 🔹 [관리자용] 특정 직원 상세 조회
    @GetMapping("/{empId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmployeeDTO> getEmployeeByAdmin(@PathVariable Long empId) {
        EmployeeDTO dto = employeeService.getEmployee(empId);
        return ResponseEntity.ok(dto);
    }


    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeDTO> updateMyInfo(
            @RequestBody @Valid EmployeeUpdateRequestDTO requestDTO,
            Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long currentEmpId = principal.getEmpId(); // ✅ 안전하게 사번 가져오기
        EmployeeDTO updated = employeeService.updateMyInfo(currentEmpId, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeDTO> getMyInfo(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long currentEmpId = principal.getEmpId(); // ✅ JWT에서 복원된 empId
        EmployeeDTO employee = employeeService.getEmployee(currentEmpId);
        return ResponseEntity.ok(employee);
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/byDepartment/{deptId}")
    public List<EmployeeDTO> getEmployeesByDepartment(@PathVariable Long deptId) {
        return employeeService.getEmployeesByDepartment(deptId);
    }



    // ★ 권한 설정: 'emp:delete' 권한이 있는 사용자만 접근 가능
    @DeleteMapping("/{empId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long empId){
        employeeService.deleteEmployee(empId);
        return ResponseEntity.noContent().build();
    }


}