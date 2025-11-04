package com.bizmate.hr.dto.role;

import com.bizmate.hr.domain.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * [역할 조회 응답 DTO]
 * - Role 목록 및 상세 정보를 프론트엔드로 전송할 때 사용
 */
@Getter
@Builder
public class RoleResponseDTO {

    private final Long roleId;
    private final String roleName;
    private final String description;

    // 해당 Role에 부여된 권한 목록 (권한명만 추출)
    private final List<String> permissionNames;

    /**
     * Entity -> DTO 변환 메서드
     */
    public static RoleResponseDTO fromEntity(Role role) {

        // Role에 연결된 Permission 엔티티에서 permiName만 추출
        List<String> permissionNames = role.getPermissions().stream()
                .map(permission -> permission.getPermName())
                .collect(Collectors.toList());

        return RoleResponseDTO.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .permissionNames(permissionNames)
                .build();
    }
}