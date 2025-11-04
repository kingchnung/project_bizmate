package com.bizmate.hr.controller;


import com.bizmate.hr.dto.role.RoleCreateRequestDTO;
import com.bizmate.hr.dto.role.RoleDTO;
import com.bizmate.hr.dto.role.RoleRequestDTO;
import com.bizmate.hr.dto.role.RoleUpdateRequestDTO;
import com.bizmate.hr.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // 역할 관리는 최고 관리자(예: 'system:manage' 권한)만 가능하도록 설정
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RoleDTO> createRole(@RequestBody @Valid RoleCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id,
                                              @RequestBody @Valid RoleUpdateRequestDTO dto) {
        return ResponseEntity.ok(roleService.updateRole(id, dto));
    }
}
