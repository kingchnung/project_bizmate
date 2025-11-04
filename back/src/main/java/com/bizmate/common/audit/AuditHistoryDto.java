package com.bizmate.common.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditHistoryDto {
    private Long revisionId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime revisionTimestamp;

    private String revisionType;    // "ADD" | "MOD" | "DEL" (문자열로 통일)
    private String modifierId;
    private String modifierName;
    private String modifierFull;    // "홍길동 (hong.gildong)"
}