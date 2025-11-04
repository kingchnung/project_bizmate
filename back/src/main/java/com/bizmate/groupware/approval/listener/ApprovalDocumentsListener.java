package com.bizmate.groupware.approval.listener;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.document.ApprovalHistory;
import com.bizmate.groupware.approval.repository.document.ApprovalHistoryRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * ✅ 전자결재 문서 생성/수정 시 자동으로 ApprovalHistory에 이력 저장
 */
@Slf4j
@Component
public class ApprovalDocumentsListener {

    private static ApprovalHistoryRepository historyRepository;

    // ✅ 리스너는 EntityListener이므로 정적 주입 필요
    @Autowired
    public void init(ApprovalHistoryRepository repository) {
        historyRepository = repository;
    }

    @PostPersist
    public void onPostPersist(ApprovalDocuments doc) {
        saveHistory(doc, "등록", "문서 생성");
    }

    @PostUpdate
    public void onPostUpdate(ApprovalDocuments doc) {
        String actionType = mapStatusToAction(doc);
        saveHistory(doc, actionType, "문서 상태 변경");
    }

    private String mapStatusToAction(ApprovalDocuments doc) {
        switch (doc.getStatus()) {
            case APPROVED:
                return "결재";
            case REJECTED:
                return "반려";
            case DELETED:
                return "삭제";
            default:
                return "수정";
        }
    }

    private void saveHistory(ApprovalDocuments doc, String actionType, String comment) {
        try {
            ApprovalHistory history = ApprovalHistory.builder()
                    .docId(doc.getDocId())
                    .actorUserId(doc.getAuthorUser() != null ? doc.getAuthorUser().getUserId() : 0L)
                    .actionType(actionType)
                    .actionComment(comment)
                    .actionTimestamp(LocalDateTime.now())
                    .build();

            historyRepository.save(history);

            log.info("🧾 [ApprovalHistory] {} | docId={} | actor={} | at={}",
                    actionType, doc.getDocId(), history.getActorUserId(), history.getActionTimestamp());

        } catch (Exception e) {
            log.error("❌ [ApprovalHistory] Failed to save history for docId={}", doc.getDocId(), e);
        }
    }
}
