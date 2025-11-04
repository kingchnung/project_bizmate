package com.bizmate.groupware.board.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BoardType {
    NOTICE("공지사항"),
    SUGGESTION("익명건의"),
    GENERAL("일반게시판");

    private final String label;

    BoardType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // ✅ 문자열을 Enum으로 변환
    @JsonCreator
    public static BoardType from(String value) {
        if (value == null) return null;
        for (BoardType t : values()) {
            if (t.name().equalsIgnoreCase(value) || t.label.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown BoardType: " + value);
    }

    // ✅ Enum을 문자열로 직렬화
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
