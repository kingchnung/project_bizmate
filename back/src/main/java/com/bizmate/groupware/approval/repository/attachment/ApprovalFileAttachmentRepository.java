package com.bizmate.groupware.approval.repository.attachment;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.attachment.ApprovalFileAttachment;
import com.bizmate.hr.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApprovalFileAttachmentRepository extends JpaRepository<ApprovalFileAttachment, Long> {
    List<ApprovalFileAttachment> findByDocument_DocId(String docId);

    /**
     * 임시 업로드 파일을 상신 시 문서에 연결
     */
    @Modifying
    @Query("UPDATE ApprovalFileAttachment f SET f.document = :document " +
            "WHERE f.uploader = :uploader AND f.document IS NULL")
    int linkPendingFiles(@Param("document") ApprovalDocuments document,
                         @Param("uploader") UserEntity uploader);

    List<ApprovalFileAttachment> findByDocumentIsNullAndUploader(UserEntity uploader);
}
