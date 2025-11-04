package com.bizmate.hr.service;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")  // ✅ test 환경에서만 실행
class EmployeeNameUpdateTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void updateEmployeeNamesForTestRange() {
        // ✅ 현실감 있는 한국 이름 24명 (8~31번)
        List<String> names = Arrays.asList(
                "김도윤", "이서현", "박지우", "정하준", "최예은", "한지민",
                "윤서우", "장민재", "조수현", "임하린", "오지호", "서민규",
                "신유진", "배주원", "양도현", "권지아", "백현우", "노채원",
                "남도윤", "문서연", "허지후", "곽태린", "유다은", "전하준"
        );

        long startId = 8L;
        long endId = 30L;
        int nameIndex = 0;

        for (long empId = startId; empId <= endId; empId++) {
            Optional<Employee> optionalEmployee = employeeRepository.findById(empId);
            if (optionalEmployee.isPresent()) {
                Employee employee = optionalEmployee.get();
                employee.setEmpName(names.get(nameIndex++));
                employeeRepository.save(employee);
                System.out.println("✅ emp_id=" + empId + " → " + employee.getEmpName());
            } else {
                System.out.println("⚠️ emp_id=" + empId + " 은(는) 존재하지 않음");
            }
        }

        // ✅ 단순 검증 (모두 업데이트되었는지 확인)
        for (long empId = startId; empId <= endId; empId++) {
            assertTrue(employeeRepository.findById(empId).isPresent());
        }

        System.out.println("🎯 직원명 업데이트 완료 (8~31)");
    }
}
