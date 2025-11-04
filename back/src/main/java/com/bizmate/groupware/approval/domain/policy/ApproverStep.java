package com.bizmate.groupware.approval.domain.policy;

import com.bizmate.groupware.approval.domain.document.Decision;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApproverStep(
        @Min(1)
        @JsonProperty("order")
        int order,              // 결재 순서(1..n)
        @JsonProperty("approverId")
        @NotNull
        String approverId,      // 결재자 사용자ID
        @JsonProperty("approverName")
        @NotBlank
        String approverName,
        @JsonProperty("decision")
        Decision decision,      // PENDING/APPROVED/REJECTED
        @JsonProperty("comment")
        String comment,         // 코멘트(반려사유 포함)
        @JsonProperty("decidedAt")
        LocalDateTime decidedAt,
        @JsonProperty("signImagePath")
        String signImagePath
) implements Serializable {
    public ApproverStep {
        approverId = (approverId == null || approverId.isBlank()) ? "-" : approverId;
        approverName = (approverName == null || approverName.isBlank()) ? "미등록 사용자" : approverName;
        decision = (decision == null) ? Decision.PENDING : decision;
        comment = (comment == null) ? "" : comment;
    }
}