// ApprovalPolicyRequest.java
package com.bizmate.groupware.approval.dto.policy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalPolicyRequest {
    private String policyName;
    private String docType;
    private String createdBy;
    private String createdDept;
    private List<ApprovalPolicyStepRequest> steps;
}
