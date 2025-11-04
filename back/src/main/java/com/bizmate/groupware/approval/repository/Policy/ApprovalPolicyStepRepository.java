package com.bizmate.groupware.approval.repository.Policy;

import com.bizmate.groupware.approval.domain.policy.ApprovalPolicyStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalPolicyStepRepository extends JpaRepository<ApprovalPolicyStep, Long> {
}
