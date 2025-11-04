package com.bizmate.hr.service;

import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.dto.code.PositionDTO;
import com.bizmate.hr.dto.code.PositionRequestDTO;

import java.util.List;
import java.util.Optional;

public interface PositionService {
    List<PositionDTO> getAllPositions();
    PositionDTO getPosition(Long positionCode);
    PositionDTO savePosition(Long positionCode, PositionRequestDTO requestDTO);
    void deletePosition(Long positionCode);
}
