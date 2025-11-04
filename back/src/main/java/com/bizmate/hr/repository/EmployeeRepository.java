package com.bizmate.hr.repository;

import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.dto.employee.EmployeeSummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmpNo(String empNo);
    List<Employee> findByEmpNameContaining(String name);
    List<Employee> findByStatus(String status);

    // ★★★ 수정: Position도 함께 Fetch Join하여 가져옵니다. ★★★
    // Position과 Department 모두 Eager Loading합니다.
    @Query("SELECT e FROM Employee e JOIN FETCH e.department JOIN FETCH e.position")
    List<Employee> findAllWithDepartmentAndPosition(); // 메서드명도 변경 (명확성을 위해)

    // ★ 수정: 특정 직원 조회 시에도 Position Fetch Join 적용
    @Query("SELECT e FROM Employee e JOIN FETCH e.department JOIN FETCH e.position WHERE e.empId = :empId")
    Optional<Employee> findByIdWithDepartmentAndPosition(@Param("empId") Long empId);

    long countByDepartment_DeptCode(String deptCode);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH e.position WHERE e.empId = :empId")
    Optional<Employee> findEmployeeDetailById(@Param("empId") Long empId);

    /**
     * 📊 나이대별 인원 통계 (JPQL 표준 함수 사용)
     * - JQL에서 지원하는 함수로 Oracle 환경에서 오류 해결
     */
    /**
     * 📊 나이대별 인원 통계 (JPQL 수정)
     */
    @Query("""
    SELECT
        CASE
            WHEN (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM e.birthDate)) < 20 THEN '20대 미만'
            WHEN (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM e.birthDate)) >= 50 THEN '50대 이상'
            ELSE CONCAT(FLOOR((EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM e.birthDate)) / 10) * 10, '대')
        END AS age_group,
        COUNT(e)
    FROM Employee e
    WHERE e.birthDate IS NOT NULL AND UPPER(e.status) <> 'RETIRED'
    GROUP BY age_group
    ORDER BY age_group
    """)
    List<Object[]> getAgeStatistics();

    /**
     * 🎖️ 직급별 인원 통계
     */
    @Query("""
            SELECT g.gradeName AS label, COUNT(e)
            FROM Employee e
            JOIN e.grade g
            WHERE UPPER(e.status) <> 'RETIRED'
            GROUP BY g.gradeName
            ORDER BY g.gradeName
            """)
    List<Object[]> getGradeStatistics();

    List<Employee> findByDepartment_DeptId(Long deptId);


    Optional<Employee> findByEmpId(Long empId);

    @Query("SELECT e FROM Employee e WHERE e.department = :dept AND e.position.positionCode = :posCode")
    Optional<Employee> findByDepartmentAndPositionCode(@Param("dept") Department dept, @Param("posCode") Long posCode);

    @Query("""
        SELECT new com.bizmate.hr.dto.employee.EmployeeSummaryDTO(
            e.empId,
            e.empNo,
            e.empName,
            g.gradeName,
            p.positionName,
            e.phone,
            e.email,
            d.deptName
        )
        FROM Employee e
        LEFT JOIN e.department d
        LEFT JOIN e.grade g
        LEFT JOIN e.position p
        """)
    List<EmployeeSummaryDTO> findEmployeeSummaries();

    Employee findByDepartment_DeptNameAndPosition_PositionName(String deptName, String positionName);

    Optional<Employee> findByEmpName(String empName);

    Optional<Employee> findFirstByDepartment_DeptCodeAndPosition_PositionCode(String deptCode, Long positionCode);
}
