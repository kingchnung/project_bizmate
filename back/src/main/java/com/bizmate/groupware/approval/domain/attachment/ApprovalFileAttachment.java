package com.bizmate.groupware.approval.domain.attachment;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.hr.domain.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "APPROVAL_ATTACHMENT",
        indexes = {
                @Index(name = "IDX_ATTACHMENT_DOC_ID", columnList = "DOC_ID")
        })
public class ApprovalFileAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // ✅ ApprovalDocuments와 FK 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOC_ID") // ApprovalDocuments.DOC_ID 참조
    private ApprovalDocuments document;

    @Column(name = "ORIGINAL_NAME", nullable = false, length = 255)
    private String originalName;

    @Column(name = "STORED_NAME", nullable = false, length = 255)
    private String storedName;

    @Column(name = "FILE_PATH", length = 255)
    private String filePath;

    @Column(name = "FILE_SIZE", nullable = false)
    private Long fileSize;

    @Column(name = "CONTENT_TYPE", length = 255)
    private String contentType;

    @Column(name = "UPLOADED_AT", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private UserEntity uploader;

    @PrePersist
    public void onCreate() {
        if (uploadedAt == null) uploadedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        if (uploadedAt == null) uploadedAt = LocalDateTime.now();
    }
}
