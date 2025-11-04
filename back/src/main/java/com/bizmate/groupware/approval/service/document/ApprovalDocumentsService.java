package com.bizmate.groupware.approval.service.document;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.groupware.approval.dto.approval.ApprovalDocumentsDto;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.UserPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ApprovalDocumentsService {

    /* ----------------------------- 작성/상신 ------------------------------ */
    ApprovalDocumentsDto draft(ApprovalDocumentsDto dto, UserDTO loginUser) throws JsonProcessingException;

    ApprovalDocumentsDto submit(ApprovalDocumentsDto dto, UserDTO loginUser) throws JsonProcessingException;

    /* -------------------------------------------------------------
   ✅ ③ 반려문서 재상신 (Resubmit)
   ------------------------------------------------------------- */
    @Transactional
    ApprovalDocumentsDto resubmit(String docId, ApprovalDocumentsDto dto, List<MultipartFile> files, UserDTO loginUser);

    /* ----------------------------- 결재/반려/삭제 ------------------------------ */
    ApprovalDocumentsDto approve(String docId, UserDTO loginUser);

    ApprovalDocumentsDto reject(String docId, UserDTO loginUser, String reason);

    void logicalDelete(String docId, UserDTO loginUser, String reason);

    /* ----------------------------- 조회 ------------------------------ */
    ApprovalDocumentsDto get(String docId);

    PageResponseDTO<ApprovalDocumentsDto> getPagedApprovals(PageRequestDTO pageRequestDTO);

    void restoreDocument(String docId);

    Map<String, Long> getApprovalSummary(String username);


    @Transactional
    void forceApprove(String docId, UserPrincipal adminUser, String reason);

    @Transactional
    void forceReject(String docId, UserPrincipal adminUser, String reason);

    PageResponseDTO<ApprovalDocumentsDto> getPagedApprovalsByStatus(PageRequestDTO pageRequestDTO, String status);

    PageResponseDTO<ApprovalDocumentsDto> getPagedAccessibleDocuments(PageRequestDTO pageRequestDTO, String username, String status);
}
