package com.bizmate.salesPages.client.controller;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.client.dto.ClientDTO;
import com.bizmate.salesPages.client.service.ClientService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales/client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping("/{clientNo}")
    public ClientDTO get(@PathVariable(name = "clientNo") Long clientNo){
        return clientService.clientGet(clientNo);
    }

    @GetMapping("/list")
    public PageResponseDTO<ClientDTO> list(PageRequestDTO pageRequestDTO){
        return clientService.clientList(pageRequestDTO);
    }

    @PostMapping(value = "/")
    public Map<String,Long> register(
            @RequestPart("clientDTO") ClientDTO clientDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        Long clientNo = clientService.clientRegister(clientDTO, file);
        return Map.of("ClientNo", clientNo);
    }

    @PutMapping("/{clientNo}")
    public Map<String,String> modify(
            @PathVariable(name = "clientNo") Long clientNo,
            @RequestPart("clientDTO") ClientDTO clientDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ){
        clientDTO.setClientNo(clientNo);
        clientService.clientModify(clientDTO, file);
        return Map.of("RESULT","SUCCESS");
    }

    @DeleteMapping("/{clientNo}")
    public Map<String,String> remove(@PathVariable(name = "clientNo") Long clientNo){
        clientService.clientRemove(clientNo);
        return Map.of("RESULT","SUCCESS");
    }

    @DeleteMapping("/list")
    public Map<String, String> removeList(@RequestBody List<Long> clientNos){
        clientService.clientRemoveList(clientNos);
        return Map.of("RESULT", "SUCCESS");
    }
}