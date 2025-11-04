package com.bizmate.groupware.approval.repository.document;

import com.bizmate.groupware.approval.domain.document.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

}
