package com.bizmate.hr.service;

import com.bizmate.hr.dto.code.GradeDTO;
import com.bizmate.hr.dto.code.GradeRequestDTO;
import java.util.List;

public interface GradeService {
    List<GradeDTO> getAllGrades();
    GradeDTO getGrade(Long gradeCode);
    GradeDTO saveGrade(Long gradeCode, GradeRequestDTO requestDTO);
    void deleteGrade(Long gradeCode);
}
