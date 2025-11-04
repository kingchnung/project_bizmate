package com.bizmate.salesPages.client.service;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.client.dto.ClientDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ClientService {
    Long clientRegister(ClientDTO clientDTO, MultipartFile file);
    ClientDTO clientGet(Long clientNo);
    void clientModify(ClientDTO clientDTO, MultipartFile file);
    void clientRemove(Long clientNo);
    PageResponseDTO<ClientDTO> clientList(PageRequestDTO pageRequestDTO);

    void clientRemoveList(List<Long> clientNos);
}
