package com.bizmate.salesPages.report.salesTarget.service;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.report.salesTarget.dto.SalesTargetDTO;

import java.util.List;

public interface SalesTargetService {
    public Long register(SalesTargetDTO salesTargetDTO);
    public SalesTargetDTO get(Long targetId);
    public void modify(SalesTargetDTO salesTargetDTO);
    public void remove(Long targetId);
    public void removeList(List<Long> targetIds);
    public PageResponseDTO<SalesTargetDTO> list(PageRequestDTO pageRequestDTO);
}
