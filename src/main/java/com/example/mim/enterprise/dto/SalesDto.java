package com.example.mim.enterprise.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SalesDto(
        Long id,
        String invoiceNo,
        LocalDateTime invoiceDate,
        Long customerId,
        String customerName,
        String deliveryToken,
        String deliveryAddress,
        String deliveryAddressBn,
        String deliveryStatus,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal dueAmount,
        String paymentStatus,
        String paymentMethod,
        Long salesPersonId,
        String salesPersonName,
        Long inventoryId,
        String inventoryName,
        LocalDateTime returnValidUntil,
        Boolean isReturned,
        List<SalesItemDto> salesItems
) {}
