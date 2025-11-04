package com.bizmate.hr.service;

import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.dto.code.GradeDTO;
import com.bizmate.hr.dto.code.GradeRequestDTO;
import com.bizmate.hr.repository.GradeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeServiceImpl implements GradeService {
    // 필요한 Repository만 주입
    private final GradeRepository gradeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getAllGrades() {
        return gradeRepository.findAll().stream()
                .map(GradeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GradeDTO getGrade(Long gradeCode) {
        Grade grade = gradeRepository.findById(gradeCode)
                .orElseThrow(() -> new EntityNotFoundException("직급 코드 " + gradeCode + "를 찾을 수 없습니다."));
        return GradeDTO.fromEntity(grade);
    }

    @Override
    public GradeDTO saveGrade(Long gradeCode, GradeRequestDTO requestDTO) {
        Grade grade;
        if (gradeCode != null) {
            grade = gradeRepository.findById(gradeCode)
                    .orElseThrow(() -> new EntityNotFoundException("직급 코드 " + gradeCode + "를 찾을 수 없습니다."));
        } else {
            grade = new Grade();
        }

        grade.setGradeName(requestDTO.getGradeName());
        grade.setIsUsed(requestDTO.getIsUsed());

        Grade saved = gradeRepository.save(grade);
        return GradeDTO.fromEntity(saved);
    }

    @Override
    public void deleteGrade(Long gradeCode) {
        gradeRepository.deleteById(gradeCode);
    }

}