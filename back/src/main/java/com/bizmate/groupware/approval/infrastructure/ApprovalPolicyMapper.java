package com.bizmate.groupware.approval.infrastructure;

import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.domain.document.Decision;
import com.bizmate.groupware.approval.domain.policy.ApprovalPolicy;
import com.bizmate.groupware.approval.domain.policy.ApprovalPolicyStep;
import com.bizmate.groupware.approval.domain.policy.ApproverStep;
import com.bizmate.groupware.approval.dto.policy.ApprovalPolicyStepRequest;
import com.bizmate.groupware.approval.dto.policy.ApprovalPolicyStepResponse;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.DepartmentRepository;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * [ApprovalPolicyMapper]
 * 결재선 정책(ApprovalPolicyStep)을 실제 결재선(ApproverStep) 객체로 변환합니다.
 * 부서코드 + 직급코드를 기반으로 실제 결재자를 매핑합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalPolicyMapper {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public List<ApprovalPolicyStep> toEntities(List<ApprovalPolicyStepRequest> stepRequests, ApprovalPolicy policy) {

        return stepRequests.stream().map(req -> {

            // ✅ 1. Employee 찾기 (empId 있으면 그대로, 없으면 부서+직급으로)
            Employee emp = Optional.ofNullable(req.getEmpId())
                    .flatMap(employeeRepository::findById)
                    .orElseGet(() -> {
                        // ✅ dept_id 기반으로 실제 dept_code 조회
                        String resolvedDeptCode = departmentRepository.findById(Long.valueOf(req.getDeptCode()))
                                .map(dept -> dept.getDeptCode()) // "11", "32" 등 반환
                                .orElse(req.getDeptCode()); // 조회 실패 시 원본 유지

                        return employeeRepository
                                .findFirstByDepartment_DeptCodeAndPosition_PositionCode(
                                        resolvedDeptCode, req.getPositionCode()
                                )
                                .orElseThrow(() ->
                                        new VerificationFailedException(
                                                "결재자 정보를 찾을 수 없습니다. (부서:" + resolvedDeptCode + ", 직위:" + req.getPositionCode() + ")"
                                        )
                                );
                    });

            // ✅ 2. ApprovalPolicyStep Entity 빌드
            return ApprovalPolicyStep.builder()
                    .stepOrder(req.getStepOrder())
                    .deptCode(req.getDeptCode())
                    .deptName(emp.getDepartment().getDeptName()) // 실제 직원의 부서명으로 덮어쓰기
                    .positionCode(req.getPositionCode())
                    .positionName(emp.getPosition().getPositionName()) // 실제 직원의 직위명으로 덮어쓰기
                    .approver(emp)
                    .approverName(emp.getEmpName()) // ✅ approver_name 저장
                    .policy(policy)
                    .build();

        }).collect(Collectors.toList());
    }

    /**
     * 🔹 Entity → Response 변환
     * 프론트로 정책 목록/상세 전송 시 사용
     */
    public List<ApprovalPolicyStepResponse> toResponses(List<ApprovalPolicyStep> steps) {
        return steps.stream()
                .map(s -> ApprovalPolicyStepResponse.builder()
                        .stepOrder(s.getStepOrder())
                        .deptName(s.getDeptName())
                        .positionName(s.getPositionName())
                        .empName(s.getApproverName()) // ✅ approver_name 그대로 출력
                        .build()
                ).collect(Collectors.toList());
    }
}
