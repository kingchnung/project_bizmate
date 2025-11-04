package com.bizmate.groupware.approval.domain.document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentType {
    REQUEST("REQUEST", "품의서"),                     // 일반 품의서
    PROJECT_PLAN("PROJECT_PLAN", "프로젝트 기획안"),   // 프로젝트 기획안
    ESTIMATE_PROPOSAL("ESTIMATE_PROPOSAL", "견적서/제안서 발송 품의"),
    EXPENSE("EXPENSE", "지출결의서"),                 // 지출결의서
    PURCHASE("PURCHASE", "구매 품의서"),              // 구매 품의서
    LEAVE("LEAVE", "휴가 신청서"),                    // 휴가신청서
    RESIGN("RESIGN", "퇴직서"),                       // 사직서
    HR_MOVE("HR_MOVE", "인사발령");                   // 인사발령서     // 인사발령/이동 신청서

    private final String code;
    private final String label;

    DocumentType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static  DocumentType from(String value) {
        if (value == null) return null;
        String v = value.trim().toUpperCase();

        for(DocumentType t : values()) {
            if(t.code.equalsIgnoreCase(v) || t.label.equalsIgnoreCase(v)) {
                return t;
            }
        }

        throw new IllegalArgumentException("유효하지 않은 문서유형: " + value);
    }

    @JsonValue // ✅ Enum → JSON 문자열 변환 (응답 시)
    public String toValue() {
        return this.code; // ← "REQUEST" 로 직렬화됨
    }

}