
package com.bizmate.groupware.approval.api.admin;

import com.bizmate.groupware.approval.dto.policy.ApprovalPolicyRequest;
import com.bizmate.groupware.approval.dto.policy.ApprovalPolicyResponse;
import com.bizmate.groupware.approval.service.policy.ApprovalPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/policies")
@RequiredArgsConstructor
public class ApprovalPolicyAdminController {

    private final ApprovalPolicyService approvalPolicyService;

    @PostMapping
    public ResponseEntity<ApprovalPolicyResponse> createPolicy(@RequestBody ApprovalPolicyRequest request) {
        return ResponseEntity.ok(approvalPolicyService.createPolicy(request));
    }

    @GetMapping
    public ResponseEntity<List<ApprovalPolicyResponse>> getAllPolicies() {
        return ResponseEntity.ok(approvalPolicyService.getAllPolicies());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApprovalPolicyResponse> updatePolicy(
            @PathVariable Long id,
            @RequestBody ApprovalPolicyRequest request) {
        return ResponseEntity.ok(approvalPolicyService.updatePolicy(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivatePolicy(@PathVariable Long id) {
        approvalPolicyService.deactivatePolicy(id);
        return ResponseEntity.ok("정책 비활성화 완료");
    }

    /* ✅ 새로 추가: 정책 재활성화 */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<String> activatePolicy(@PathVariable Long id) {
        approvalPolicyService.activatePolicy(id);
        return ResponseEntity.ok("정책이 다시 활성화되었습니다.");
    }

    /* ✅ 새로 추가: 정책 완전 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePolicy(@PathVariable Long id) {
        approvalPolicyService.deletePolicy(id);
        return ResponseEntity.ok("정책이 완전히 삭제되었습니다.");
    }


}
