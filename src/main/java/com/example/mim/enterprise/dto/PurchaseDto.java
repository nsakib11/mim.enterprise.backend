package com.example.mim.enterprise.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseDto(
        Long id,
        String purchaseOrderNo,
        LocalDateTime orderDate,
        LocalDateTime expectedDeliveryDate,
        Long supplierId,
        String supplierName,
        String quotationNo,
        String supplierInvoiceNo,
        String paymentType,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal dueAmount,
        String paymentStatus,
        String deliveryStatus,
        BigDecimal totalOrderedQuantity,
        BigDecimal totalDeliveredQuantity,
        BigDecimal totalPendingQuantity,
        boolean priceLocked,
        List<PurchaseItemDto> purchaseItems
) {}
