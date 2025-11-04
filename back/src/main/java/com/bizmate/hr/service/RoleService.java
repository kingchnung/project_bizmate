package com.bizmate.hr.service;

import com.bizmate.hr.dto.role.RoleCreateRequestDTO;
import com.bizmate.hr.dto.role.RoleDTO;
import com.bizmate.hr.dto.role.RoleRequestDTO;
import com.bizmate.hr.dto.role.RoleUpdateRequestDTO;

import java.util.List;

public interface RoleService {
    RoleDTO createRole(RoleCreateRequestDTO dto);
    List<RoleDTO> getAllRoles();
    RoleDTO getRole(Long roleId);
    RoleDTO updateRole(Long id, RoleUpdateRequestDTO dto);
}
