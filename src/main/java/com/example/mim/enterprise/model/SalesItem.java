// SalesItem.java
package com.example.mim.enterprise.model;

import com.example.mim.enterprise.model.enums.ProductCategory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sales_items")
@Getter @Setter @NoArgsConstructor
public class SalesItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id")
    private Sales sales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    // Product categories for accounting separation
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory; // HARDWARE, BOARD, BOARD_HARDWARE

    // Delivery tracking
    private BigDecimal deliveredQuantity;
    private BigDecimal pendingQuantity;

    private void calculateTotals() {
        if (quantity != null && unitPrice != null) {
            this.totalPrice = quantity.multiply(unitPrice);
        }
        if (deliveredQuantity != null && quantity != null) {
            this.pendingQuantity = quantity.subtract(deliveredQuantity);
        }
    }

}