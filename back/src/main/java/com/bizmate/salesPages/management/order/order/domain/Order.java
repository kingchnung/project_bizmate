package com.bizmate.salesPages.management.order.order.domain;

import com.bizmate.salesPages.client.domain.Client;
import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@Entity
@Table(name = "ORDER_MASTER")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Audited
@SequenceGenerator(
        name = "ORDER_SEQ_GENERATOR",
        sequenceName = "ORDER_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class Order implements Serializable {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ORDER_SEQ_GENERATOR"
    )
    private Long orderNo;

    @Column(unique = true, nullable = false, length = 20)
    private String orderId;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private LocalDate orderIdDate;

    @Temporal(TemporalType.DATE)
    private LocalDate orderDate;

    private String projectId;
    private String projectName;
    private LocalDate orderDueDate;
    private BigDecimal orderAmount;
    private BigDecimal totalSubAmount;
    private BigDecimal totalVatAmount;
    private String userId;
    private String writer;

    @ManyToOne(fetch = FetchType.LAZY)
    // Client의 PK가 'clientNo'이므로,
    // Order 테이블의 외래 키 컬럼 이름도 'CLIENT_NO'일 가능성이 높습니다.
    @JoinColumn(name = "CLIENT_NO") // <-- DB의 실제 외래 키 컬럼 이름
    @NotAudited
    private Client client;

    private String orderNote;

    private String orderStatus;

    @Builder.Default
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @NotAudited
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem){
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void calculateOrderAmount(){
        if(this.orderItems == null || this.orderItems.isEmpty()){
            this.orderAmount = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
            this.totalSubAmount = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
            this.totalVatAmount = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
            return;
        }

        BigDecimal subAmountSum = BigDecimal.ZERO;
        BigDecimal vatAmountSum = BigDecimal.ZERO;

        for(OrderItem item : this.orderItems){
            if(item.getUnitPrice() != null && item.getQuantity() != null){
                BigDecimal itemSubTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                subAmountSum = subAmountSum.add(itemSubTotal);

                if(item.getUnitVat() != null){
                    BigDecimal itemTotalVat = item.getUnitVat().multiply(BigDecimal.valueOf(item.getQuantity()));
                    vatAmountSum = vatAmountSum.add(itemTotalVat);
                }
            }
        }

        this.totalSubAmount = subAmountSum.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.totalVatAmount = vatAmountSum.setScale(2, BigDecimal.ROUND_HALF_UP);

        this.orderAmount = this.totalSubAmount.add(this.totalVatAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void updateOrderItems(List<OrderItem> newOrderItems){
        this.orderItems.clear();

        if(newOrderItems != null){
            for(OrderItem orderItem : newOrderItems){
                orderItem.calculateAmount();
                this.addOrderItem(orderItem);
            }
        }
    }

    public void changeProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void changeOrderDueDate(LocalDate orderDueDate) {
        this.orderDueDate = orderDueDate;
    }

    public void changeOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void changeClientId(Client client) {
        this.client = client;
    }

    public void changeOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public void changeOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
