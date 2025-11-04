package com.bizmate.salesPages.management.collections.service;



import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.management.collections.dto.CollectionDTO;
import com.bizmate.salesPages.report.salesReport.dto.*;

import java.util.List;

public interface CollectionService {
    public String register(CollectionDTO collectionDTO);
    public CollectionDTO get(String collectionId);
    public void modify(CollectionDTO collectionDTO);
    public void remove(String collectionId);
    public PageResponseDTO<CollectionDTO> list(PageRequestDTO pageRequestDTO);


    List<CollectionDTO> listByClient(String clientId);
}
