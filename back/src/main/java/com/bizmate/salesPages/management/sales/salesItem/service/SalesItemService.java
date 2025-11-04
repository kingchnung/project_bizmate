package com.bizmate.salesPages.management.sales.salesItem.service;

import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;

import java.util.List;

public interface SalesItemService {
    List<SalesItem> salesItemList(SalesItem salesItem);
    SalesItem salesItemInsert(SalesItem salesItem);
    SalesItem salesItemUpdate(SalesItem salesItem);
    void salesItemDelete(SalesItem salesItem);
}
