package com.bizmate.hr.service;

import com.bizmate.hr.dto.permission.PermissionDTO;
import com.bizmate.hr.dto.permission.PermissionRequestDTO;

import java.util.List;

public interface PermissionService {
    PermissionDTO savePermission(Long permId, PermissionRequestDTO requestDTO);
    List<PermissionDTO> getAllPermissions();
    PermissionDTO getPermission(Long permId);
}
