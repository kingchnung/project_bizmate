package com.bizmate.hr.dto.role;


import com.bizmate.hr.domain.Permission;
import com.bizmate.hr.domain.Role;
import com.bizmate.hr.dto.permission.PermissionDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class RoleDTO {
    private Long roleId;
    private String roleName;
    private String description;
    private String isUsed;

    @Builder.Default
    private Set<PermissionDTO> permissions = new HashSet<>();

    public static RoleDTO fromEntity(Role role) {
        if (role == null) return null;

        // 권한 리스트 매핑 추가
        Set<PermissionDTO> permissionDTOs = role.getPermissions()
                .stream()
                .map(p -> PermissionDTO.builder()
                        .permId(p.getPermId())
                        .permName(p.getPermName())
                        .description(p.getDescription())
                        .isUsed(p.getIsUsed())
                        .build())
                .collect(Collectors.toSet());

        return RoleDTO.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .isUsed(role.getIsUsed())
                .permissions(permissionDTOs)
                .build();
    }
}

