// PurchaseItem.java
package com.example.mim.enterprise.model;

import com.example.mim.enterprise.model.enums.ProductCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase_items")
@Getter @Setter @NoArgsConstructor
public class PurchaseItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    private BigDecimal orderedQuantity;
    private BigDecimal deliveredQuantity;
    private BigDecimal pendingQuantity;

    private BigDecimal purchasePrice;
    private BigDecimal salesPriceMin; // Sales price range minimum
    private BigDecimal salesPriceMax; // Sales price range maximum

    private BigDecimal currentPurchasePrice; // For price changes during delivery

    // Inventory tracking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Inventory inventory;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory; // HARDWARE, BOARD, BOTH

    private void calculateQuantities() {
        if (orderedQuantity != null && deliveredQuantity != null) {
            this.pendingQuantity = orderedQuantity.subtract(deliveredQuantity);
        }

        // If price is not locked, use current purchase price for calculations
        if (purchase != null && !purchase.isPriceLocked()) {
            this.currentPurchasePrice = purchasePrice;
        }
    }


}