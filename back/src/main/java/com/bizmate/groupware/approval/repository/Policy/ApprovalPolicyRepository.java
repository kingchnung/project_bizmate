package com.bizmate.groupware.approval.repository.Policy;

import com.bizmate.groupware.approval.domain.policy.ApprovalPolicy;
import com.bizmate.groupware.approval.domain.document.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalPolicyRepository extends JpaRepository<ApprovalPolicy, Long> {
    List<ApprovalPolicy> findAll();
    Optional<ApprovalPolicy> findByDocTypeAndIsActiveTrue(DocumentType docType);
}
