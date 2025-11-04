package com.bizmate.groupware.approval.dto.policy;

public record ApprovalPolicyListRecord (
        Long id,
        String policyName,
        String docType,
        String departmentName,
        boolean isActive
){
}
