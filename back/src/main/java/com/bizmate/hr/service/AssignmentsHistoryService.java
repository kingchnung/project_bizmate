package com.bizmate.hr.service;
import com.bizmate.hr.dto.assignment.AssignmentHistoryDTO;
import com.bizmate.hr.dto.assignment.AssignmentHistoryRequestDTO;

import java.util.List;

public interface AssignmentsHistoryService {
    List<AssignmentHistoryDTO> getAllHistories();

    AssignmentHistoryDTO createAssignment(AssignmentHistoryRequestDTO requestDTO, String createdByUsername);

    // 🔹 직원별 이력 조회
    List<AssignmentHistoryDTO> getHistoryByEmployee(Long empId);

    // 🔹 부서별 이력 조회
    List<AssignmentHistoryDTO> getHistoryByDepartment(Long deptId);
}