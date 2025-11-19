package com.example.mim.enterprise.dto;

import java.math.BigDecimal;

public record PurchaseItemDto(
        Long id,
        Long productId,
        String productName,
        BigDecimal orderedQuantity,
        BigDecimal deliveredQuantity,
        BigDecimal pendingQuantity,
        BigDecimal purchasePrice,
        BigDecimal salesPriceMin,
        BigDecimal salesPriceMax,
        BigDecimal currentPurchasePrice,
        String productCategory
) {}
