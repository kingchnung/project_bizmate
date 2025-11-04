package com.bizmate.groupware.approval.dto.approval;

import com.bizmate.groupware.approval.domain.document.Decision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproverDto {

    @NotBlank(message = "결재자 ID는 필수입니다.")
    private String approverId;
    @NotNull(message = "결재 순서는 필수입니다.")
    private int order;

    private Decision decision; //PENDING,APPROVED/REJECTED
    private String comment;
}
