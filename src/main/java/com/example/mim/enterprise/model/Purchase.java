// Purchase.java
package com.example.mim.enterprise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchases")
@Getter @Setter @NoArgsConstructor
public class Purchase extends BaseEntity {

    @Column(unique = true)
    private String purchaseOrderNo;

    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Supplier supplier;

    // Quotation and Invoice References
    private String quotationNo;
    private String supplierInvoiceNo;

    // Payment Information
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType; // CASH, CREDIT

    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // PAID, PARTIAL, DUE

    // Delivery Status
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus; // PENDING, PARTIAL, COMPLETED

    private BigDecimal totalOrderedQuantity;
    private BigDecimal totalDeliveredQuantity;
    private BigDecimal totalPendingQuantity;

    // Price Lock Information
    private boolean priceLocked; // Some suppliers lock price after invoice

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<PurchaseItem> purchaseItems = new ArrayList<>();

    private void calculateTotals() {
        // Calculate totals from purchase items
        if (purchaseItems != null && !purchaseItems.isEmpty()) {
            this.totalOrderedQuantity = purchaseItems.stream()
                .map(PurchaseItem::getOrderedQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            this.totalDeliveredQuantity = purchaseItems.stream()
                .map(PurchaseItem::getDeliveredQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            this.totalPendingQuantity = totalOrderedQuantity.subtract(totalDeliveredQuantity);
        }
    }
}