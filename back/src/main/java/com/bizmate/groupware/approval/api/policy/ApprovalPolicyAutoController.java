package com.bizmate.groupware.approval.api.policy;

import com.bizmate.groupware.approval.domain.document.DocumentType;
import com.bizmate.groupware.approval.domain.policy.ApprovalPolicy;
import com.bizmate.groupware.approval.domain.policy.ApprovalPolicyStep;
import com.bizmate.groupware.approval.dto.policy.ApprovalPolicyStepResponse;
import com.bizmate.groupware.approval.repository.Policy.ApprovalPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/approvals/policy")
@RequiredArgsConstructor
public class ApprovalPolicyAutoController {

    private final ApprovalPolicyRepository approvalPolicyRepository;

    /**
     * ✅ 자동결재선 조회 API
     * - 문서유형 + 부서코드 기준으로 정책을 찾는다.
     * - 활성화된 정책(isActive = true)만 대상으로 한다.
     * - React에서는 /approvals/policy/auto-line?docType=...&deptCode=... 형태로 호출
     */
    @GetMapping("/auto-line")
    public ResponseEntity<?> getAutoApprovalLine(
            @RequestParam String docType,
            @RequestParam(required = false) String deptCode
    ) {
        log.info("📡 자동결재선 조회 요청: docType={}, deptCode={}", docType, deptCode);

        Optional<ApprovalPolicy> optionalPolicy =
                approvalPolicyRepository.findByDocTypeAndIsActiveTrue(DocumentType.valueOf(docType));

        if (optionalPolicy.isEmpty()) {
            log.info("❌ 활성화된 결재정책이 없습니다. (문서유형: {})", docType);
            return ResponseEntity.ok(List.of());
        }

        ApprovalPolicy policy = optionalPolicy.get();

        // ✅ Step 목록을 DTO로 변환
        List<ApprovalPolicyStepResponse> steps = policy.getSteps().stream()
                .sorted((a, b) -> Integer.compare(a.getStepOrder(), b.getStepOrder()))
                .map(this::toResponse)
                .collect(Collectors.toList());

        log.info("✅ 자동결재선 {}건 반환 (문서유형: {})", steps.size(), docType);
        return ResponseEntity.ok(steps);
    }

    // 🔹 Entity → DTO 변환
    private ApprovalPolicyStepResponse toResponse(ApprovalPolicyStep s) {
        return ApprovalPolicyStepResponse.builder()
                .stepOrder(s.getStepOrder())
                .deptName(s.getDeptName())
                .positionName(s.getPositionName())
                .empName(s.getApprover() != null ? s.getApprover().getEmpName() : null)
                .build();
    }
}
