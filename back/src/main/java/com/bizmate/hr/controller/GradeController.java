package com.bizmate.hr.controller;

import com.bizmate.hr.dto.code.GradeDTO;
import com.bizmate.hr.dto.code.GradeRequestDTO;
import com.bizmate.hr.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    // READ - All
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<GradeDTO> getAllGrades() {
        return gradeService.getAllGrades();
    }

    // READ - One
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<GradeDTO> getGrade(@PathVariable("id") Long gradeCode) {
        return ResponseEntity.ok(gradeService.getGrade(gradeCode));
    }

    // CREATE
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GradeDTO> createGrade(@RequestBody @Valid GradeRequestDTO requestDTO) {
        GradeDTO created = gradeService.saveGrade(null, requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GradeDTO> updateGrade(@PathVariable("id") Long gradeCode,
                                                @RequestBody @Valid GradeRequestDTO requestDTO) {
        GradeDTO updated = gradeService.saveGrade(gradeCode, requestDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteGrade(@PathVariable("id") Long gradeCode) {
        gradeService.deleteGrade(gradeCode);
        return ResponseEntity.noContent().build();
    }
}
