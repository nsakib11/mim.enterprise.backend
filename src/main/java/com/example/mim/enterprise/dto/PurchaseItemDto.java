package com.example.mim.enterprise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemDto {

    private Long id;
    private Long productId;
    private String productName;

    private Long inventoryId;

    private BigDecimal orderedQuantity;
    private BigDecimal deliveredQuantity;
    private BigDecimal pendingQuantity;

    private BigDecimal purchasePrice;
    private BigDecimal salesPriceMin;
    private BigDecimal salesPriceMax;

    private BigDecimal currentPurchasePrice;

    private String productCategory;
}
