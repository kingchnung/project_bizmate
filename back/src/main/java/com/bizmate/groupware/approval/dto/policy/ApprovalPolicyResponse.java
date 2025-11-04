// ApprovalPolicyResponse.java
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
public class ApprovalPolicyResponse {
    private Long id;
    private String policyName;
    private String docType;
    private String departmentName;
    private List<ApprovalPolicyStepResponse> steps;
    private boolean isActive;
}
