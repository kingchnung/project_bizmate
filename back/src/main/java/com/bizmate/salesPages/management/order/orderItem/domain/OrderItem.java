package com.bizmate.salesPages.management.order.orderItem.domain;

import com.bizmate.salesPages.management.order.order.domain.Order;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ORDER_ITEM")
@SequenceGenerator(
        name = "ORDER_ITEM_SEQ_GENERATOR",
        sequenceName = "ORDER_ITEM_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class OrderItem {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ORDER_ITEM_SEQ_GENERATOR"
    )
    private Long orderItemId;

    private String itemName;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal unitVat;
    private BigDecimal totalAmount;
    private String itemNote;
    private Integer lineNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "orderId",  nullable = false)
    @JsonBackReference
    private Order order;

    @Column(name = "ORDER_NO", nullable = false)
    private String orderNo;

    public void setOrder(Order order){
        this.order = order;
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
        if(this.unitPrice != null && this.quantity != null && this.quantity > 0) {
            if(this.unitVat == null || this.unitVat.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal tenPercent = new BigDecimal("0.1");

                if (this.unitPrice.compareTo(BigDecimal.ZERO) > 0) {
                    this.unitVat = this.unitPrice.multiply(tenPercent)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                } else{
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
