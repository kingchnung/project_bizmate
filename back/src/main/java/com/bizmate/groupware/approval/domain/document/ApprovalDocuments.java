package com.bizmate.groupware.approval.domain.document;

import com.bizmate.common.domain.BaseEntity;
import com.bizmate.groupware.approval.domain.attachment.ApprovalFileAttachment;
import com.bizmate.groupware.approval.domain.policy.ApproverStep;
import com.bizmate.groupware.approval.infrastructure.ApproverLineJsonConverter;
import com.bizmate.groupware.approval.infrastructure.JsonMapConverter;
import com.bizmate.groupware.approval.infrastructure.DocumentTypeConverter;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.Role;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.UserPrincipal;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.*;

/**
 * ✅ ApprovalDocuments (전자결재 문서)
 * - 문서 생성, 상신, 승인, 반려, 삭제 상태 관리
 * - HR 모듈 (User, Employee, Role, Department) 참조
 * - markApproved(), markRejected(), markDeleted() 포함
 */
@Entity
@Getter
@Setter
@Table(name = "APPROVAL_DOCUMENTS")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDocuments extends BaseEntity {

    /* ----------------------------- 기본 필드 ------------------------------ */

    @Id
    @Column(name = "DOC_ID", length = 40, nullable = false)
    private String docId;

    @Convert(converter = DocumentTypeConverter.class)
    @Column(name = "DOC_TYPE", nullable = false)
    private DocumentType docType;

    @Column(name = "DOC_TITLE", nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "DOC_STATUS", nullable = false, length = 20)
    private DocumentStatus status;

    @Column(name = "FINAL_DOC_NUMBER", unique = true)
    @Comment("최종 승인 시 발급되는 문서번호(연속 시퀀스)")
    private String finalDocNumber;

    /* ----------------------------- HR 참조 ------------------------------ */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DEPT_ID", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private UserEntity authorUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID")
    private Role authorRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID")
    private Employee authorEmployee;


    /* ----------------------------- 결재 내용 ------------------------------ */

    @Lob
    @Column(name = "APPROVAL_LINE", nullable = false)
    @Convert(converter = ApproverLineJsonConverter.class)
    private List<ApproverStep> approvalLine;

    @Lob
    @Column(name = "DOC_DATA")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> docContent;

    @Column(name = "CURRENT_APPROVER_INDEX")
    @ColumnDefault("0")
    private int currentApproverIndex;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "APPROVAL_DOC_VIEWERS", joinColumns = @JoinColumn(name = "DOC_ID"))
    @Column(name = "VIEWER_ID", length = 40)
    private List<String> viewerIds = new ArrayList<>();

    /* ----------------------------- 첨부파일 ------------------------------ */

    @OneToMany(mappedBy = "document",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<ApprovalFileAttachment> attachments = new ArrayList<>();

    /* ----------------------------- 결재 이력 필드 ------------------------------ */

    @Column(name = "APPROVED_BY")
    private String approvedBy;

    @Column(name = "APPROVED_EMP_ID")
    private Long approvedEmpId;

    @Column(name = "APPROVED_DATE")
    private LocalDateTime approvedDate;

    @Column(name = "REJECTED_BY")
    private String rejectedBy;

    @Column(name = "REJECTED_EMP_ID")
    private Long rejectedEmpId;

    @Column(name = "REJECTED_REASON")
    private String rejectedReason;

    @Column(name = "REJECTED_DATE")
    private LocalDateTime rejectedDate;

    @Column(name = "DELETED_BY")
    private String deletedBy;

    @Column(name = "DELETED_EMP_ID")
    private Long deletedEmpId;

    @Column(name = "DELETED_REASON")
    private String deletedReason;

    @Column(name = "DELETED_DATE")
    private LocalDateTime deletedDate;

    @Version
    @Column(name = "VERSION")
    private Long version;

    /* ----------------------------- 상태 검증 ------------------------------ */

    public boolean canApprove() {
        return this.status == DocumentStatus.IN_PROGRESS;
    }

    public boolean canReject() {
        return this.status == DocumentStatus.IN_PROGRESS;
    }

    public boolean isDeletable() {
        return this.status == DocumentStatus.DRAFT || this.status == DocumentStatus.REJECTED;
    }

    /* ----------------------------- Auditing 래퍼 ------------------------------ */

    public void markCreated(UserDTO user) {
        super.setCreatedBy(user.getUsername());
        super.setUpdatedBy(user.getUsername());
        super.setCreatedAt(LocalDateTime.now());
        super.setUpdatedAt(LocalDateTime.now());
    }

    public void markUpdated(UserDTO user) {
        super.setUpdatedBy(user.getEmpName());
        super.setUpdatedAt(LocalDateTime.now());
    }

    /* ----------------------------- 비즈니스 로직 ------------------------------ */

    /** ✅ 강제 승인 (관리자 전용) */
    public void forceApprove(UserPrincipal adminUser, String reason) {
        this.status = DocumentStatus.APPROVED;
        this.approvedBy = "[강제승인]" + adminUser.getEmpName();
        this.approvedEmpId = adminUser.getEmpId();
        this.approvedDate = LocalDateTime.now();
        this.rejectedReason = null; // 혹시 반려 이유가 남아있다면 초기화
        this.currentApproverIndex = approvalLine != null ? approvalLine.size() : 0;

        // Auditing 갱신
        super.setUpdatedBy(adminUser.getUsername());
        super.setUpdatedAt(LocalDateTime.now());
    }

    /** ✅ 강제 반려 (관리자 전용) */
    public void forceReject(UserPrincipal adminUser, String reason) {
        this.status = DocumentStatus.REJECTED;
        this.rejectedBy = "[강제반려]" + adminUser.getEmpName();
        this.rejectedEmpId = adminUser.getEmpId();
        this.rejectedReason = reason;
        this.rejectedDate = LocalDateTime.now();

        // Auditing 갱신
        super.setUpdatedBy(adminUser.getUsername());
        super.setUpdatedAt(LocalDateTime.now());
    }

    /** ✅ 논리삭제 처리 */
    public void markDeleted(UserDTO user, String reason) {
        if (!isDeletable())
            throw new IllegalStateException("DRAFT 또는 REJECTED 상태의 문서만 삭제할 수 있습니다.");

        this.currentApproverIndex = 0;
        this.status = DocumentStatus.DELETED;
        this.deletedBy = user.getEmpName();
        this.deletedEmpId = user.getEmpId();
        this.deletedReason = reason;
        this.deletedDate = LocalDateTime.now();

        markUpdated(user);
    }

    public void moveToNextApprover() {
        if (this.currentApproverIndex + 1 < this.approvalLine.size()) {
            this.currentApproverIndex++;
        }
    }
}
