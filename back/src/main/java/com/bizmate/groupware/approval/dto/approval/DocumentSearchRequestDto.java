package com.bizmate.groupware.approval.dto.approval;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentSearchRequestDto {
    private String keyword;      // 제목, 내용 검색
    private String status;       // 상태별 필터
    private Long departmentId;   // 부서 필터
    private int page = 0;
    private int size = 10;
}
