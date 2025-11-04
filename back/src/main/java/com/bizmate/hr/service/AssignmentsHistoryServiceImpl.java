package com.bizmate.hr.service;

import com.bizmate.hr.domain.AssignmentsHistory;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.dto.assignment.AssignmentHistoryDTO;
import com.bizmate.hr.dto.assignment.AssignmentHistoryRequestDTO;
import com.bizmate.hr.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentsHistoryServiceImpl implements AssignmentsHistoryService {

    private final AssignmentsHistoryRepository historyRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public List<AssignmentHistoryDTO> getAllHistories() {
        return historyRepository.findAll().stream()
                .map(AssignmentHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentHistoryDTO> getHistoryByEmployee(Long empId) {
        return historyRepository.findByEmployee_EmpIdOrderByAssDateDesc(empId)
                .stream()
                .map(AssignmentHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentHistoryDTO> getHistoryByDepartment(Long deptId) {
        return historyRepository.findByNewDepartment_DeptIdOrderByAssDateDesc(deptId)
                .stream()
                .map(AssignmentHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 발령 등록 (관리자 전용)
     */
    @Override
    public AssignmentHistoryDTO createAssignment(AssignmentHistoryRequestDTO dto, String createdByUsername) {

        // 1. 필수 관계 엔티티 조회
        Employee employee = employeeRepository.findById(dto.getEmpId())
                .orElseThrow(() -> new EntityNotFoundException("직원 ID " + dto.getEmpId() + "를 찾을 수 없습니다."));

        Department newDept = departmentRepository.findById(dto.getNewDeptId())
                .orElseThrow(() -> new EntityNotFoundException("신규 부서 ID " + dto.getNewDeptId() + "를 찾을 수 없습니다."));

        Position newPosition = positionRepository.findById(dto.getNewPositionCode())
                .orElseThrow(() -> new EntityNotFoundException("신규 직책코드 " + dto.getNewPositionCode() + "를 찾을 수 없습니다."));

        Grade newGrade = gradeRepository.findById(dto.getNewGradeCode())
                .orElseThrow(() -> new EntityNotFoundException("신규 직급코드 " + dto.getNewGradeCode() + "를 찾을 수 없습니다."));

        // 2. 이전 정보는 Optional
        Department prevDept = employee.getDepartment();

        Position prevPosition = employee.getPosition();

        Grade prevGrade = employee.getGrade();

        // 3. 등록자 조회
        UserEntity createdBy = userRepository.findByUsername(createdByUsername)
                .orElseThrow(() -> new EntityNotFoundException("등록자 계정(" + createdByUsername + ")을 찾을 수 없습니다."));

        // 4. 엔티티 생성 (Builder)
        AssignmentsHistory history = AssignmentsHistory.builder()
                .employee(employee)
                .assDate(dto.getAssDate())
                .reason(dto.getReason())
                .previousDepartment(prevDept)
                .previousPosition(prevPosition)
                .previousGrade(prevGrade)
                .newDepartment(newDept)
                .newPosition(newPosition)
                .newGrade(newGrade)
                .createdBy(createdBy)
                .build();

        // 5. 저장 후 DTO 변환
        historyRepository.save(history);

        employee.setDepartment(newDept);
        employee.setPosition(newPosition);
        employee.setGrade(newGrade);
        employeeRepository.save(employee);

        return AssignmentHistoryDTO.fromEntity(history);
    }
}
