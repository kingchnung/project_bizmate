package com.bizmate.hr.repository;

import com.bizmate.hr.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findAllByOrderByDeptCodeAsc();
    Optional<Department> findByDeptCode(String deptCode);
    List<Department> findByParentDept_DeptId(Long parentDeptId);
    // JPQL을 사용하여 끝자리가 '0'인 본부 코드 중 가장 큰 값을 찾습니다.
    @Query("SELECT MAX(CAST(d.deptCode AS integer)) FROM Department d WHERE d.deptCode LIKE '%0'")
    Integer findMaxDivisionCode();

    // 특정 접두사로 시작하고 끝자리가 '0'이 아닌 팀 코드 중 가장 큰 값을 찾습니다.
    @Query("SELECT MAX(CAST(d.deptCode AS integer)) FROM Department d WHERE d.deptCode LIKE :prefix% AND d.deptCode NOT LIKE '%0'")
    Integer findMaxTeamCode(@Param("prefix") String prefix);
}
