package com.bizmate.groupware.approval.service.PDF;

import com.bizmate.groupware.approval.domain.PDF.EmployeeSignature;
import com.bizmate.groupware.approval.repository.PDF.EmployeeSignatureRepository;
import com.bizmate.hr.domain.Employee;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeSignatureService {

    private static final String SIGNATURE_PATH = "src/main/resources/signatures/";
    private final EmployeeSignatureRepository signatureRepository;

    public String getSignaturePath(Employee employee) {
        String empNo = employee.getEmpNo();
        File empSignFile = new File(SIGNATURE_PATH + empNo + ".png");

        // 🔹 1. 사번 기반 경로 우선
        if (empSignFile.exists()) {
            return empSignFile.getAbsolutePath();
        }

        // 🔹 2. DB에 저장된 경로 fallback
        return signatureRepository.findByEmployee(employee)
                .map(EmployeeSignature::getSignImagePath)
                .filter(path -> new File(path).exists())
                .orElse(null);
    }
}
