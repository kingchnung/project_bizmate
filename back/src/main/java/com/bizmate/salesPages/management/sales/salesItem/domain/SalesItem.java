package com.bizmate.salesPages.management.sales.salesItem.domain;

import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "SALES_ITEM")
@SequenceGenerator(
        name = "SALES_ITEM_SEQ_GENERATOR",
        sequenceName = "SALES_ITEM_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class SalesItem {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SALES_ITEM_SEQ_GENERATOR"
    )
    private Long salesItemId;

    private String itemName;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal unitVat;
    private BigDecimal totalAmount;
    private String itemNote;
    private Integer lineNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id", referencedColumnName = "salesId", nullable = false)
    @JsonBackReference
    private Sales sales;

    @Column(name = "SALES_NO") // (NOT NULL일 수 있으니 확인 필요)
    private String salesNo;

    public void setSales(Sales sales){
        this.sales = sales;
    }

    public void changeItemName(String itemName) {
        this.itemName = itemName;
    }

    public void changeQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void changeUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void changeUnitVat(BigDecimal unitVat) {
        this.unitVat = unitVat;
    }

    public void changeTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void changeItemNote(String itemNote) {
        this.itemNote = itemNote;
    }

    public void calculateAmount() {
        if(this.unitPrice != null && this.quantity != null && this.quantity > 0){
            if(this.unitVat == null || this.unitVat.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal tenPercent = new BigDecimal("0.1");

                if (this.unitPrice.compareTo(BigDecimal.ZERO) > 0) {
                    this.unitVat = this.unitPrice.multiply(tenPercent)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                } else {
                    this.unitVat = BigDecimal.ZERO;
                }
            }
            BigDecimal subTotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
            BigDecimal totalVat = this.unitVat.multiply(BigDecimal.valueOf(this.quantity));
            this.totalAmount = subTotal.add(totalVat).setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.unitVat = BigDecimal.ZERO;
            this.totalAmount = BigDecimal.ZERO;
        }
    }
}
