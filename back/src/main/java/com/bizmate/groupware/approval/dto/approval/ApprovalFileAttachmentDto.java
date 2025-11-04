package com.bizmate.groupware.approval.dto.approval;

import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.attachment.ApprovalFileAttachment;
import com.bizmate.hr.domain.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalFileAttachmentDto {
    private Long id;
    private String originalName;
    private String storedName;
    private String filePath;
    private Long fileSize;
    private String contentType;
    private Long uploaderId;
    private String uploaderName;
    private LocalDateTime uploadedAt;

    public static ApprovalFileAttachmentDto fromEntity(ApprovalFileAttachment entity) {
        return ApprovalFileAttachmentDto.builder()
                .id(entity.getId())
                .originalName(entity.getOriginalName())
                .storedName(entity.getStoredName())
                .filePath(entity.getFilePath())
                .fileSize(entity.getFileSize())
                .contentType(entity.getContentType())
                .uploadedAt(entity.getUploadedAt()) // ✅ 추가!
                .uploaderId(entity.getUploader() != null ? entity.getUploader().getUserId() : null)
                .uploaderName(entity.getUploader() != null ? entity.getUploader().getEmpName() : "-")
                .build();
    }

    public ApprovalFileAttachment toEntity(ApprovalDocuments document, UserEntity uploader) {
        if (document == null || document.getDocId() == null)
            throw new VerificationFailedException("📎 문서 연결이 누락되었습니다 (DOC_ID=null).");

        // ✅ 파일 메타정보 설정
        ApprovalFileAttachment entity = new ApprovalFileAttachment();

        entity.setDocument(document); // ✅ FK 연결 (필수)
        entity.setUploader(uploader);
        entity.setOriginalName(originalName);
        entity.setStoredName(storedName);
        entity.setFilePath(filePath);
        entity.setFileSize(fileSize != null ? fileSize : 0L);
        entity.setContentType(contentType);
        entity.setUploadedAt(LocalDateTime.now());

        return entity;
    }
}
