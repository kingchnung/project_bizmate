package com.bizmate.hr.dto.code;

import com.bizmate.hr.domain.code.Position;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PositionDTO {
    private Long positionCode; // ★ 필드명 변경
    private String positionName;
    private String description; // ★ 새로운 필드
    private String isUsed; // ★ 새로운 필드

    public static PositionDTO fromEntity(Position position) {
        if(position == null) return null;

        return PositionDTO.builder()
                .positionCode(position.getPositionCode())
                .positionName(position.getPositionName())
                .description(position.getDescription())
                .isUsed(position.getIsUsed())
                .build();
    }
}
