package com.bizmate.hr.service;

import com.bizmate.hr.dto.department.*;

import java.util.List;

public interface DepartmentService {

    List<DepartmentOverviewDTO> getDepartmentOverview();  // 현황 조회
    List<DepartmentResponseDTO> getAllDepartments();              // 전체 목록 조회
    DepartmentResponseDTO getDepartmentDetail(Long deptId);  // 상세 조회
    DepartmentResponseDTO createDepartment(DepartmentCreateRequestDTO dto); // 생성
    DepartmentResponseDTO updateDepartment(Long deptId, DepartmentUpdateRequestDTO dto); // 수정
    void deleteDepartment(Long deptId);                   // 삭제
    void permanentlyDeleteDepartment(Long deptId);
    DepartmentDTO assignManager(Long deptId, Long managerId);
}
