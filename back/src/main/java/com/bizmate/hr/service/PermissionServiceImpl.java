package com.bizmate.hr.service;

import com.bizmate.hr.domain.Permission;
import com.bizmate.hr.dto.permission.PermissionDTO;
import com.bizmate.hr.dto.permission.PermissionRequestDTO;
import com.bizmate.hr.repository.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionDTO savePermission(Long permId, PermissionRequestDTO requestDTO) {
        Permission permission = new Permission();
        if(permId != null){
            permission = permissionRepository.findById(permId)
                    .orElseThrow(()->new EntityNotFoundException("권한 ID " + permId + "를 찾을 수 없습니다."));
        }else{
            permission=new Permission();
        }
        permission.setPermName(requestDTO.getPermName());

        Permission savedPermission = permissionRepository.save(permission);
        return PermissionDTO.fromEntity(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionDTO getPermission(Long permId) {
        Permission permission = permissionRepository.findById(permId)
                .orElseThrow(()->new EntityNotFoundException("권한 ID " + permId + "를 찾을 수 없습니다."));

        return PermissionDTO.fromEntity(permission);
    }


}
