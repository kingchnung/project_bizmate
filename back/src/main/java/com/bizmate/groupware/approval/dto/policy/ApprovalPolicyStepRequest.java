// ApprovalPolicyStepRequest.java
package com.bizmate.groupware.approval.dto.policy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalPolicyStepRequest {
    private int stepOrder;
    private String deptCode;
    private String deptName;
    private Long positionCode;
    private Long empId;
}
