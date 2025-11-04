package com.bizmate.groupware.approval.repository.PDF;

import com.bizmate.groupware.approval.domain.PDF.EmployeeSignature;
import com.bizmate.hr.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeSignatureRepository extends JpaRepository<EmployeeSignature, Long> {
    Optional<EmployeeSignature> findByEmployeeEmpId(Long empId);

    Optional<EmployeeSignature> findByEmployeeEmpNo(String empNo);

    Optional<EmployeeSignature> findByEmployee(Employee employee);
}
