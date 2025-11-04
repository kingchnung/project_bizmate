package com.bizmate.hr.service;

import com.bizmate.hr.domain.Permission;
import com.bizmate.hr.domain.Role;
import com.bizmate.hr.dto.role.RoleCreateRequestDTO;
import com.bizmate.hr.dto.role.RoleDTO;
import com.bizmate.hr.dto.role.RoleRequestDTO;
import com.bizmate.hr.dto.role.RoleUpdateRequestDTO;
import com.bizmate.hr.repository.PermissionRepository;
import com.bizmate.hr.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public RoleDTO createRole(RoleCreateRequestDTO dto) {
        Role role = new Role();
        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
        role.setIsUsed(dto.getIsUsed());

        // ✅ 권한 매핑
        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = dto.getPermissionIds().stream()
                    .map(id -> permissionRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("권한 ID " + id + "를 찾을 수 없습니다.")))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        return RoleDTO.fromEntity(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles(){
        return roleRepository.findAll().stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()->new EntityNotFoundException("역할 ID " + roleId + "를 찾을 수 없습니다."));
        return RoleDTO.fromEntity(role);
    }

    @Override
    public RoleDTO updateRole(Long id, RoleUpdateRequestDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("역할 ID " + id + "를 찾을 수 없습니다."));

        if (dto.getRoleName() != null) role.setRoleName(dto.getRoleName());
        if (dto.getDescription() != null) role.setDescription(dto.getDescription());
        if (dto.getIsUsed() != null) role.setIsUsed(dto.getIsUsed());

        // ✅ 권한 매핑 업데이트
        if (dto.getPermissionIds() != null) {
            Set<Permission> permissions = dto.getPermissionIds().stream()
                    .map(pid -> permissionRepository.findById(pid)
                            .orElseThrow(() -> new EntityNotFoundException("권한 ID " + pid + "를 찾을 수 없습니다.")))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }

        Role updatedRole = roleRepository.save(role);
        return RoleDTO.fromEntity(updatedRole);
    }
}
