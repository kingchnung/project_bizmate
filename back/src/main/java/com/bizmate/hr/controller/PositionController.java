package com.bizmate.hr.controller;

import com.bizmate.hr.dto.code.PositionDTO;
import com.bizmate.hr.dto.code.PositionRequestDTO;
import com.bizmate.hr.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<PositionDTO> getAllPositions() {
        return positionService.getAllPositions();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<PositionDTO> getPosition(@PathVariable("id") Long positionCode) {
        return ResponseEntity.ok(positionService.getPosition(positionCode));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PositionDTO> createPosition(@RequestBody @Valid PositionRequestDTO requestDTO) {
        PositionDTO created = positionService.savePosition(null, requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PositionDTO> updatePosition(@PathVariable("id") Long positionCode,
                                                      @RequestBody @Valid PositionRequestDTO requestDTO) {
        PositionDTO updated = positionService.savePosition(positionCode, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deletePosition(@PathVariable("id") Long positionCode) {
        positionService.deletePosition(positionCode);
        return ResponseEntity.noContent().build();
    }
}
