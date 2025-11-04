package com.bizmate.hr.repository;

import com.bizmate.hr.domain.AssignmentsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentsHistoryRepository extends JpaRepository<AssignmentsHistory, Long> {
    List<AssignmentsHistory> findByEmployee_EmpId(Long empId);
    // 부서별 이력 조회 (관리자용)
    List<AssignmentsHistory> findByNewDepartment_DeptId(Long deptId);

    // 🔹 특정 직원의 이동 이력 조회
    List<AssignmentsHistory> findByEmployee_EmpIdOrderByAssDateDesc(Long empId);

    // 🔹 특정 부서(신규부서 기준)의 이동 이력 조회
    List<AssignmentsHistory> findByNewDepartment_DeptIdOrderByAssDateDesc(Long deptId);

}
