package com.bizmate.hr.controller;


import com.bizmate.hr.dto.permission.PermissionDTO;
import com.bizmate.hr.dto.permission.PermissionRequestDTO;
import com.bizmate.hr.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<PermissionDTO> getAllPermissions() {
        return permissionService.getAllPermissions();
    }
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PermissionDTO> createPermission(@RequestBody @Valid PermissionRequestDTO requestDTO) {
        PermissionDTO createdDto = permissionService.savePermission(null, requestDTO);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PermissionDTO> updatePermission(@PathVariable Long id,
                                                          @RequestBody @Valid PermissionRequestDTO requestDTO) {
        PermissionDTO updatedDto = permissionService.savePermission(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

}
