package com.example.mim.enterprise.dto;

import java.math.BigDecimal;

public record SalesItemDto(
        Long id,
        Long productId,
        String productName,
        BigDecimal quantity,
        BigDecimal deliveredQuantity,
        BigDecimal pendingQuantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        String productCategory
) {}
