package com.bizmate.groupware.approval.service.document;


import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.hr.dto.user.UserDTO;

public interface ApprovalHistoryService {
    void saveHistory(ApprovalDocuments doc, UserDTO actor, String actionType, String comment);
}
