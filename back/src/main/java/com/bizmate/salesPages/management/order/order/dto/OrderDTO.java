package com.bizmate.salesPages.management.order.order.dto;

import com.bizmate.salesPages.client.domain.Client;
import com.bizmate.salesPages.management.order.orderItem.dto.OrderItemDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long orderNo;

    private String orderId;
    private LocalDate orderIdDate;
    private LocalDate orderDate;
    private String projectId;
    private String projectName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate orderDueDate;

    private BigDecimal orderAmount;
    private BigDecimal totalSubAmount;
    private BigDecimal totalVatAmount;
    private String userId;
    private String writer;
    private Client clientId;
    private Client clientCompany;
    private String orderNote;
    private String orderStatus;

    private List<OrderItemDTO> orderItems;
}
