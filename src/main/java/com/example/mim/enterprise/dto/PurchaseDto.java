package com.example.mim.enterprise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto {

    private Long id;
    private String purchaseOrderNo;

    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;

    private Long supplierId;
    private String supplierName;

    private String quotationNo;
    private String supplierInvoiceNo;

    private String paymentType;

    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;

    private String paymentStatus;
    private String deliveryStatus;

    private BigDecimal totalOrderedQuantity;
    private BigDecimal totalDeliveredQuantity;
    private BigDecimal totalPendingQuantity;

    private boolean priceLocked;

    private List<PurchaseItemDto> purchaseItems;
}
