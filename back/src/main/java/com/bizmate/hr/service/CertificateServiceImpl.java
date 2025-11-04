package com.bizmate.hr.service;


import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.util.PdfGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService{
    private final EmployeeRepository employeeRepository;
    private final PdfGenerator pdfGenerator;

    @Override
    public byte[] generateEmploymentCertificate(Long empId) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new EntityNotFoundException("직원 정보를 찾을 수 없습니다."));
        return pdfGenerator.createEmploymentCertificate(emp);
    }
}
