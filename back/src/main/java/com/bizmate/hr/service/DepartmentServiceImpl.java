package com.bizmate.hr.service;

import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.dto.department.*;
import com.bizmate.hr.repository.DepartmentRepository;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    /** 📊 부서현황 조회 */
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentOverviewDTO> getDepartmentOverview() {
        List<Department> departments = departmentRepository.findAllByOrderByDeptCodeAsc();

        return departments.stream().map(dept -> {
            int empCount = dept.getEmployees().size();
            double avgAge = dept.getEmployees().stream()
                    .filter(e -> e.getBirthDate() != null)
                    .mapToInt(e -> Period.between(e.getBirthDate(), LocalDate.now()).getYears())
                    .average().orElse(0);
            double avgYears = dept.getEmployees().stream()
                    .filter(e -> e.getStartDate() != null)
                    .mapToInt(e -> Period.between(e.getStartDate(), LocalDate.now()).getYears())
                    .average().orElse(0);

            return DepartmentOverviewDTO.builder()
                    .deptId(dept.getDeptId())
                    .deptName(dept.getDeptName())
                    .deptCode(dept.getDeptCode())
                    .parentDeptId(dept.getParentDept() != null ? dept.getParentDept().getDeptId() : null)
                    .employeeCount(empCount)
                    .avgAge(Math.round(avgAge * 10) / 10.0)
                    .avgYears(Math.round(avgYears * 10) / 10.0)
                    .build();
        }).collect(Collectors.toList());
    }

    /** 📋 전체 부서 조회 */
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAllByOrderByDeptCodeAsc()
                .stream()
                .map(DepartmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** 📋 부서 상세조회 */
    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentDetail(Long deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다. ID=" + deptId));
        return DepartmentResponseDTO.fromEntity(dept);
    }

    /** 🏗️ 부서 생성 */
    @Override
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentCreateRequestDTO dto) {
        log.info("--- 1. '부서 생성' 서비스 시작 ---");
        log.info("▶ 받은 요청 데이터(DTO): deptName={}, parentDeptId={}", dto.getDeptName(), dto.getParentDeptId());

        try {
            log.info("--- 2. 부서 코드 생성 로직 진입 ---");
            String nextCode = generateNextDeptCode(dto.getParentDeptId());
            log.info("▶ 생성된 다음 부서 코드: {}", nextCode);

            log.info("--- 3. 상위 부서 엔티티 조회 시도 ---");
            Department parentDepartment = null;
            if (dto.getParentDeptId() != null) {
                parentDepartment = departmentRepository.findById(dto.getParentDeptId())
                        .orElseThrow(() -> new EntityNotFoundException("상위 부서를 찾을 수 없습니다."));
            }
            log.info("▶ 상위 부서 엔티티: {}", (parentDepartment != null ? parentDepartment.getDeptName() : "없음"));

            log.info("--- 4. 새 부서 엔티티 생성 시도 ---");
            Department newDepartment = Department.builder()
                    .deptCode(nextCode)
                    .deptName(dto.getDeptName())
                    .parentDept(parentDepartment)
                    // isUsed와 creDate는 @Builder.Default로 처리되므로 여기서 명시할 필요 없음
                    .build();
            log.info("▶ 생성된 엔티티 (저장 전): {}", newDepartment);

            log.info("--- 5. 데이터베이스에 저장 시도 ---");
            departmentRepository.save(newDepartment);
            log.info("✅ --- 6. 저장 성공! --- ✅");

            return DepartmentResponseDTO.fromEntity(newDepartment);

        } catch (Exception e) {
            log.error("🔥🔥🔥 '부서 생성' 중 심각한 오류 발생! 🔥🔥🔥", e);
            // 오류를 다시 던져서 @Transactional이 롤백을 수행하도록 함
            throw e;
        }
    }

    /** ✏️ 부서 수정 */
    @Override
    @Transactional
    public DepartmentResponseDTO updateDepartment(Long deptId, DepartmentUpdateRequestDTO dto) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다. ID=" + deptId));

        dept.setDeptName(dto.getDeptName());
        dept.setIsUsed(dto.getIsUsed());

        if (dto.getParentDeptId() != null) {
            Department parentDept = departmentRepository.findById(dto.getParentDeptId())
                    .orElseThrow(() -> new RuntimeException("상위 부서를 찾을 수 없습니다."));
            dept.setParentDept(parentDept);
        } else {
            // 상위 부서 없음으로 변경 (최상위 본부로 승격)
            dept.setParentDept(null);
        }
        departmentRepository.save(dept);
        return DepartmentResponseDTO.fromEntity(dept);
    }

    //부서장임명
    @Override
    @Transactional
    public DepartmentDTO assignManager(Long deptId, Long managerId) {
        Department department = departmentRepository.findById(deptId)
                .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다. ID=" + deptId));

        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다. ID=" + managerId));

        department.setManager(manager);
        departmentRepository.save(department);

        log.info("👔 부서 [{}]의 부서장이 [{}]로 임명되었습니다.", department.getDeptName(), manager.getEmpName());

        return DepartmentDTO.fromEntity(department);
    }


    /**
     * ✏️ 부서 비활성화 (Soft Delete)
     * 기존의 deleteDepartment 메서드의 역할을 변경합니다.
     */
    @Override
    @Transactional
    public void deleteDepartment(Long deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다. ID=" + deptId));

        // ✅ 1. 안전 확인: 활성화된 직원이 있는지 확인 (중요)
        boolean hasActiveEmployees = dept.getEmployees().stream()
                .anyMatch(e -> "ACTIVE".equalsIgnoreCase(e.getStatus()));
        if (hasActiveEmployees) {
            throw new IllegalStateException("현재 근무 중인 직원이 소속된 부서는 비활성화할 수 없습니다.");
        }

        // ✅ 2. 실제 삭제 대신, isUsed 플래그를 'N'으로 변경
        dept.setIsUsed("N");
        departmentRepository.save(dept); // 변경사항 저장

        log.info("🟡 부서 비활성화 완료: {}", dept.getDeptName());
    }

    /**
     * 🗑️ 부서 영구 삭제 (Hard Delete) - 신규 추가
     */
    @Override
    @Transactional
    public void permanentlyDeleteDepartment(Long deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다. ID=" + deptId));

        // ✅ 3. 영구 삭제를 위한 더 강력한 안전 확인 로직
        if (!dept.getEmployees().isEmpty()) {
            throw new IllegalStateException("소속된 직원이 있는 부서는 영구 삭제할 수 없습니다. 먼저 직원들을 다른 부서로 이동시켜주세요.");
        }
        if (!dept.getChildDepts().isEmpty()) {
            throw new IllegalStateException("하위 부서(팀)가 있는 부서는 영구 삭제할 수 없습니다. 하위 부서를 먼저 처리해주세요.");
        }

        departmentRepository.delete(dept);
        log.warn("🗑️🔥 부서 영구 삭제 완료: {}", dept.getDeptName());
    }

    /** 🧮 부서코드 자동 생성 규칙 */
    private String generateNextDeptCode(Long parentDeptId) {
        if (parentDeptId == null) {
            // --- 상위 부서 없음 (본부 생성) ---
            Integer maxCode = departmentRepository.findMaxDivisionCode();
            // 만약 본부가 하나도 없으면 maxCode는 null이 됩니다.
            int nextCode = (maxCode == null) ? 10 : ((maxCode / 10) + 1) * 10;
            return String.valueOf(nextCode);
        } else {
            // --- 하위 부서 있음 (팀 생성) ---
            Department parent = departmentRepository.findById(parentDeptId)
                    .orElseThrow(() -> new EntityNotFoundException("상위 부서를 찾을 수 없습니다."));

            // 상위 부서 코드의 앞자리 (예: "30" -> "3")
            String prefix = parent.getDeptCode().substring(0, parent.getDeptCode().length() - 1);

            Integer maxCode = departmentRepository.findMaxTeamCode(prefix);
            // 만약 해당 본부의 팀이 하나도 없으면 maxCode는 null이 됩니다.
            int nextCode = (maxCode == null) ? Integer.parseInt(prefix + "1") : maxCode + 1;
            return String.valueOf(nextCode);
        }
    }
}
