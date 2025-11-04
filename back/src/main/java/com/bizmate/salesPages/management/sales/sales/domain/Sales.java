package com.bizmate.salesPages.management.sales.sales.domain;

import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SALES")
@Builder
@Audited
@SequenceGenerator(
        name = "SALES_SEQ_GENERATOR",
        sequenceName = "SALES_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class Sales {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SALES_SEQ_GENERATOR"
    )
    private Long salesNo;

    @Column(unique = true, nullable = false, length = 20)
    private String salesId;

    @CreationTimestamp
    private LocalDate salesIdDate;
    private LocalDate salesDate;

    private String projectId;
    private String projectName;
    private LocalDate deploymentDate;
    private BigDecimal salesAmount;
    private BigDecimal totalSubAmount;
    private BigDecimal totalVatAmount;
    private String userId;
    private String writer;
    private String clientId;
    private String clientCompany;
    private String salesNote;
    private boolean invoiceIssued;

    @Builder.Default
    @OneToMany(
            mappedBy = "sales",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @NotAudited
    private List<SalesItem> salesItems = new ArrayList<>();

    public void addSalesItem(SalesItem salesItem){
        this.salesItems.add(salesItem);
        salesItem.setSales(this);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_no")
    @ToString.Exclude
    private Order order;

    public void calculateSalesAmount(){
        if(this.salesItems == null || this.salesItems.isEmpty()){
            this.salesAmount = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
            this.totalSubAmount = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
            this.totalVatAmount = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
            return;
        }

        BigDecimal subAmountSum = BigDecimal.ZERO;
        BigDecimal vatAmountSum = BigDecimal.ZERO;

        for(SalesItem item : this.salesItems){
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

        this.salesAmount = this.totalSubAmount.add(this.totalVatAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void updateSalesItems(List<SalesItem> newSalesItems){
        this.salesItems.clear();

        if(newSalesItems != null){
            for(SalesItem salesItem : newSalesItems){
                salesItem.calculateAmount();
                this.addSalesItem(salesItem);
            }
        }
    }

    public void changeProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void changeDeploymentDate(LocalDate deploymentDate) {
        this.deploymentDate = deploymentDate;
    }

    public void changeSalesDate(LocalDate salesDate) {
        this.salesDate = salesDate;
    }

    public void changeInvoiceIssued(boolean invoiceIssued) {
        this.invoiceIssued = invoiceIssued;
    }

    public void changeClientId(String clientId) {
        this.clientId = clientId;
    }

    public void changeSalesNote(String salesNote) {
        this.salesNote = salesNote;
    }
}
