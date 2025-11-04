package com.bizmate.hr.service;

import com.bizmate.hr.dto.employee.*;

import java.util.List;

public interface EmployeeService { // ★ 인터페이스 이름 변경

    EmployeeDTO createEmployee(EmployeeCreateRequestDTO createDTO);
    EmployeeDTO updateEmployee(Long EmpId, EmployeeUpdateRequestDTO updateDTO);
    EmployeeDTO retireEmployee(Long EmpId);
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO getEmployee(Long empId);
    void deleteEmployee(Long empId);
    EmployeeDetailDTO getEmployeeDetail(Long empId);
    EmployeeDTO updateMyInfo(Long empId, EmployeeUpdateRequestDTO requestDTO);
    String generateEmpNo(String deptCode);
    List<EmployeeStatisticDTO> getAgeStatistics();
    List<EmployeeStatisticDTO> getGradeStatistics();
    List<EmployeeDTO> getEmployeesByDepartment(Long deptId);
    List<EmployeeDTO> getActiveEmployees();
    List<EmployeeSummaryDTO> getEmployeeSummaries();
}