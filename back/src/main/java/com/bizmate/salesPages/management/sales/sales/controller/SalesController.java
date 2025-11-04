package com.bizmate.salesPages.management.sales.sales.controller;


import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.management.sales.sales.dto.SalesDTO;
import com.bizmate.salesPages.management.sales.sales.service.SalesService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales/sales")
@RequiredArgsConstructor
public class SalesController {
    private final SalesService salesService;

    @GetMapping("/{salesId}")
    public SalesDTO get(@PathVariable(name = "salesId") String salesId){
        return salesService.get(salesId);
    }

    @GetMapping("/list")
    public PageResponseDTO<SalesDTO> List(PageRequestDTO pageRequestDTO){
        return salesService.list(pageRequestDTO);
    }

    @PostMapping("/")
    public Map<String, String> register(@RequestBody SalesDTO salesDTO){
        String salesId = salesService.register(salesDTO);
        return Map.of("SalesId", salesId);
    }

    @PutMapping("/{salesId}")
    public Map<String, String> modify(@PathVariable(name = "salesId") String salesId, @RequestBody SalesDTO salesDTO){
        salesDTO.setSalesId(salesId);
        salesService.modify(salesDTO);
        return Map.of("RESULT","SUCCESS");
    }

    @DeleteMapping("/{salesId}")
    public Map<String,String> remove(@PathVariable(name = "salesId")String salesId){
        salesService.remove(salesId);
        return Map.of("RESULT","SUCCESS");
    }

    @GetMapping("/client/{clientId}")
    public List<SalesDTO> listByClient(@PathVariable("clientId") String clientId) {
        return salesService.listByClient(clientId);
    }
}
