package com.bizmate.salesPages.report.salesTarget.service;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.salesPages.report.salesTarget.domain.SalesTarget;
import com.bizmate.salesPages.report.salesTarget.dto.SalesTargetDTO;
import com.bizmate.salesPages.report.salesTarget.repository.SalesTargetRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesTargetServiceImpl implements SalesTargetService {
    private final SalesTargetRepository salesTargetRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long register(SalesTargetDTO salesTargetDTO) {
        Optional<SalesTarget> existingTarget = salesTargetRepository.findByTargetYearAndTargetMonth(
                salesTargetDTO.getTargetYear(),
                salesTargetDTO.getTargetMonth()
        );

        if (existingTarget.isPresent()) {
            throw new IllegalStateException(
                    String.format("%d년 %d월의 매출 목표는 이미 등록되어 있습니다.",
                            salesTargetDTO.getTargetYear(),
                            salesTargetDTO.getTargetMonth()
                    )
            );
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            salesTargetDTO.setUserId(userPrincipal.getUsername());
            salesTargetDTO.setWriter(userPrincipal.getEmpName());
        } else {
            throw new IllegalStateException("주문 등록을 위한 사용자 인증 정보를 찾을 수 없습니다. (비정상 접근)");
        }

        SalesTarget salesTarget = modelMapper.map(salesTargetDTO, SalesTarget.class);
        SalesTarget savedSalesTarget = salesTargetRepository.save(salesTarget);
        return savedSalesTarget.getTargetId();
    }

    @Override
    public SalesTargetDTO get(Long targetId) {
        Optional<SalesTarget> result = salesTargetRepository.findById(targetId);
        SalesTarget salesTarget = result.orElseThrow();
        SalesTargetDTO dto = modelMapper.map(salesTarget, SalesTargetDTO.class);
        return dto;
    }

    @Override
    public void modify(SalesTargetDTO salesTargetDTO) {
        Optional<SalesTarget> result = salesTargetRepository.findById(salesTargetDTO.getTargetId());
        SalesTarget salesTarget = result.orElseThrow();

        salesTarget.changTargetYear(salesTargetDTO.getTargetYear());
        salesTarget.changeTargetMonth(salesTargetDTO.getTargetMonth());
        salesTarget.changeTargetAmount(salesTargetDTO.getTargetAmount());

        salesTargetRepository.save(salesTarget);
    }

    @Override
    public void remove(Long targetId) {
        salesTargetRepository.deleteById(targetId);
    }

    @Override
    public void removeList(List<Long> targetIds) {
        salesTargetRepository.deleteAllByIdInBatch(targetIds);
    }


//    @Override
//    public PageResponseDTO<SalesTargetDTO> list(PageRequestDTO pageRequestDTO) {
//        Pageable pageable = PageRequest.of(
//                pageRequestDTO.getPage() - 1,
//                pageRequestDTO.getSize(),
//                Sort.by("targetId").descending());
//
//        Page<SalesTarget> result = salesTargetRepository.findAll(pageable);
//        List<SalesTargetDTO> dtoList = result.getContent().stream().map(
//                salesTarget -> modelMapper.map(salesTarget, SalesTargetDTO.class)).collect(Collectors.toList());
//        long totalCount = result.getTotalElements();
//
//        PageResponseDTO<SalesTargetDTO> responseDTO = PageResponseDTO.<SalesTargetDTO>withAll().dtoList(dtoList).pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();
//
//        return responseDTO;
//    }

    @Override
    public PageResponseDTO<SalesTargetDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("targetYear").descending().and(Sort.by("targetMonth").ascending())
        );

        Page<SalesTarget> result;

        Integer targetYear = pageRequestDTO.getYear();

        if (targetYear != null && targetYear > 0) {
            result = salesTargetRepository.findByTargetYear(targetYear, pageable);
        } else {
            result = salesTargetRepository.findAll(pageable);
        }

        List<SalesTargetDTO> dtoList = result.getContent().stream().map(
                salesTarget -> modelMapper.map(salesTarget, SalesTargetDTO.class)).collect(Collectors.toList());
        long totalCount = result.getTotalElements();

        PageResponseDTO<SalesTargetDTO> responseDTO = PageResponseDTO.<SalesTargetDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();

        return responseDTO;
    }
}