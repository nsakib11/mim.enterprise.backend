// Sales.java
package com.example.mim.enterprise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Getter @Setter @NoArgsConstructor
public class Sales extends BaseEntity {

    @Column(unique = true)
    private String invoiceNo;

    private LocalDateTime invoiceDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Customer customer;

    // Delivery Information
    private String deliveryToken;
    private String deliveryAddress;
    private String deliveryAddressBn;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus; // PENDING, PARTIAL, COMPLETED

    // Payment Information
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // PAID, PARTIAL, DUE

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // CASH, CHEQUE, BANK_TRANSFER

    // Sales Person and Inventory Reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_person_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Employee salesPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Inventory inventory;

    // Return Policy
    private LocalDateTime returnValidUntil;
    private Boolean isReturned = false;

    @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<SalesItem> salesItems = new ArrayList<>();



}