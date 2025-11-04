package com.bizmate.groupware.approval.service.document;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.document.ApprovalHistory;
import com.bizmate.groupware.approval.repository.document.ApprovalHistoryRepository;
import com.bizmate.hr.dto.user.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalHistoryServiceImpl implements ApprovalHistoryService{

    private final ApprovalHistoryRepository approvalHistoryRepository;

    @Override
    @Transactional
    public void saveHistory(ApprovalDocuments doc, UserDTO actor, String actionType, String comment) {
        try {
            ApprovalHistory history = ApprovalHistory.builder()
                    .docId(doc.getDocId())
                    .actorUserId(actor.getUserId())
                    .actionType(actionType)
                    .actionComment(comment)
                    .actionTimestamp(LocalDateTime.now())
                    .build();

            approvalHistoryRepository.save(history);

            log.info("🧾 [ApprovalHistory] {} | docId={} | by={} | at={}",
                    actionType, doc.getDocId(), actor.getEmpName(), history.getActionTimestamp());
        } catch (Exception e) {
            log.error("❌ [ApprovalHistory] 저장 실패 | docId={}", doc.getDocId(), e);
        }
    }
}
