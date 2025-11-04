package com.bizmate.salesPages.management.sales.salesItem.controller;

import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import com.bizmate.salesPages.management.sales.salesItem.service.SalesItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales/salesItems")
@RequiredArgsConstructor
public class SalesItemController {
    private final SalesItemService salesItemService;

    @GetMapping("/all/{salesId}")
    public List<SalesItem> salesItemList(@PathVariable String salesId, SalesItem salesItem, Sales sales){
        sales.setSalesId(salesId);
        salesItem.setSales(sales);
        List<SalesItem> salesItemList = salesItemService.salesItemList(salesItem);
        return salesItemList;
    }

    @PostMapping(value = "/insert", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SalesItem salesItemInsert(@RequestBody SalesItem salesItem){
        SalesItem result = salesItemService.salesItemInsert(salesItem);
        return result;
    }

    @PutMapping(value = "/{salesItemId}", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SalesItem salesItemUpdate(@PathVariable Long salesItemId, @RequestBody SalesItem salesItem, Sales sales){
        salesItem.setSalesItemId(salesItemId);
        SalesItem result = salesItemService.salesItemUpdate(salesItem);
        return result;
    }

    @DeleteMapping(value = "/{salesItemId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public void salesItemDelete(@PathVariable Long salesItemId, SalesItem salesItem){
        salesItem.setSalesItemId(salesItemId);
        salesItemService.salesItemDelete(salesItem);
    }
}
