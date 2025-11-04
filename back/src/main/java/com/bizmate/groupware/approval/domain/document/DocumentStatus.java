package com.bizmate.groupware.approval.domain.document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentStatus {
    DRAFT("임시저장"),          // 임시저장(기안중)
    IN_PROGRESS("결재대기"),    // 결재 진행 중
    REJECTED("반려"),       // 반려
    APPROVED("승인"),       // 최종 승인 완료
    ARCHIVED("폐기"),       // 보존/폐기로 사용자 목록에서 숨김
    DELETED("삭제");       // 명확히 폐기 상태로 별도 구분하고 싶을 때

    private final String label; // ✅ 한글 라벨

    public static DocumentStatus from(String code) {
        for (DocumentStatus status : values()) {
            if (status.name().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid DocumentStatus: " + code);
    }
}
