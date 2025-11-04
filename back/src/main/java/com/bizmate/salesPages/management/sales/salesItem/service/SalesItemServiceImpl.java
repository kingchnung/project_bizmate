package com.bizmate.salesPages.management.sales.salesItem.service;

import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import com.bizmate.salesPages.management.sales.salesItem.repository.SalesItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalesItemServiceImpl implements SalesItemService{
    private final SalesItemRepository salesItemRepository;


    @Override
    public List<SalesItem> salesItemList(SalesItem salesItem) {
        List<SalesItem> salesItemList = salesItemRepository.salesIdSalesItemList(salesItem.getSales().getSalesId());
        return salesItemList;
    }
    @Override
    public SalesItem salesItemInsert(SalesItem salesItem) {
        Sales sales = salesItem.getSales();
        Optional<Integer> maxLineNumOptional = salesItemRepository.findMaxLineNumByOrder(sales);

        int newLineNum = maxLineNumOptional
                .map(max -> max +1)
                .orElse(1);

        salesItem.setLineNum(newLineNum);

        SalesItem result = salesItemRepository.save(salesItem);
        return result;
    }

    @Override
    @Transactional
    public SalesItem salesItemUpdate(SalesItem salesItem) {
        Optional<SalesItem> salesItemOptional = salesItemRepository.findById(salesItem.getSalesItemId());
        SalesItem updateSalesItem = salesItemOptional.orElseThrow(
                ()-> new NoSuchElementException("SalesItem ID [" + salesItem.getSalesItemId() + "]를 찾을 수 없습니다.")
        );

        updateSalesItem.changeItemName(salesItem.getItemName());
        updateSalesItem.changeQuantity(salesItem.getQuantity());
        updateSalesItem.changeUnitPrice(salesItem.getUnitPrice());
        updateSalesItem.changeUnitVat(salesItem.getUnitVat());
        updateSalesItem.changeTotalAmount(salesItem.getTotalAmount());
        updateSalesItem.changeItemNote(salesItem.getItemNote());

        SalesItem result = salesItemRepository.save(updateSalesItem);
        return result;
    }

    @Override
    public void salesItemDelete(SalesItem salesItem) {
        salesItemRepository.deleteById(salesItem.getSalesItemId());
    }
}
