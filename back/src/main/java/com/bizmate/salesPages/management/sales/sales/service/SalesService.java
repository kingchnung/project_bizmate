package com.bizmate.salesPages.management.sales.sales.service;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.management.sales.sales.dto.SalesDTO;

import java.util.List;

public interface SalesService {
    public String register(SalesDTO salesDTO);
    public SalesDTO get(String salesId);
    public void modify(SalesDTO salesDTO);
    public void remove(String salesId);
    public PageResponseDTO<SalesDTO> list(PageRequestDTO pageRequestDTO);

    List<SalesDTO> listByClient(String clientId);
}

