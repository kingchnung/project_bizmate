package com.bizmate.groupware.approval.domain.policy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApproveRejectRequest(
    @NotNull Long actorUserId,
    @NotBlank String comment
) {}
