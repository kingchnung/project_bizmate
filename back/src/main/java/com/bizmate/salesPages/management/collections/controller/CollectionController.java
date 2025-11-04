package com.bizmate.salesPages.management.collections.controller;



import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.management.collections.dto.CollectionDTO;
import com.bizmate.salesPages.management.collections.service.CollectionService;
import com.bizmate.salesPages.report.salesReport.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    /**
     * 🔒 ID 패턴 제한: YYYYMMDD-#### 형태만 매칭
     *  -> /receivables 같은 경로가 여기로 빨려들어가지 않음
     */
    @GetMapping("/{collectionId:\\d{8}-\\d{4}}")
    public CollectionDTO get(@PathVariable String collectionId) {
        return collectionService.get(collectionId);
    }

    @GetMapping("/list")
    public PageResponseDTO<CollectionDTO> List(PageRequestDTO pageRequestDTO) {
        return collectionService.list(pageRequestDTO);
    }

    @PostMapping(value = "/")
    public Map<String, String> register(@RequestBody CollectionDTO collectionDTO) {
        String collectionId = collectionService.register(collectionDTO);
        return Map.of("CollectionId", collectionId);
    }

    @PutMapping("/{collectionId:\\d{8}-\\d{4}}")
    public Map<String, String> modify(@PathVariable String collectionId,
                                      @RequestBody CollectionDTO collectionDTO) {
        collectionDTO.setCollectionId(collectionId);
        collectionService.modify(collectionDTO);
        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{collectionId:\\d{8}-\\d{4}}")
    public Map<String, String> remove(@PathVariable String collectionId) {
        collectionService.remove(collectionId);
        return Map.of("RESULT", "SUCCESS");
    }

    @GetMapping("/client/{clientId}")
    public List<CollectionDTO> listByClient(@PathVariable("clientId") String clientId) {
        return collectionService.listByClient(clientId);
    }
}
