package com.bizmate.hr.service;

import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.dto.code.PositionDTO;
import com.bizmate.hr.dto.code.PositionRequestDTO;
import com.bizmate.hr.repository.PositionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PositionDTO> getAllPositions() {
        return positionRepository.findAll().stream()
                .map(PositionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PositionDTO getPosition(Long positionCode) {
        Position position = positionRepository.findById(positionCode)
                .orElseThrow(() -> new EntityNotFoundException("직책 코드 " + positionCode + "를 찾을 수 없습니다."));
        return PositionDTO.fromEntity(position);
    }

    @Override
    public PositionDTO savePosition(Long positionCode, PositionRequestDTO requestDTO) {
        Position position;
        if (positionCode != null) {
            position = positionRepository.findById(positionCode)
                    .orElseThrow(() -> new EntityNotFoundException("직책 코드 " + positionCode + "를 찾을 수 없습니다."));
        } else {
            position = new Position();
        }

        position.setPositionName(requestDTO.getPositionName());
        position.setDescription(requestDTO.getDescription());
        position.setIsUsed(requestDTO.getIsUsed());

        Position saved = positionRepository.save(position);
        return PositionDTO.fromEntity(saved);
    }

    @Override
    public void deletePosition(Long positionCode) {
        positionRepository.deleteById(positionCode);
    }
}
