package com.example.mim.enterprise.dto;

import com.example.mim.enterprise.model.PaymentMethod;
import com.example.mim.enterprise.model.PaymentStatus;
import com.example.mim.enterprise.model.ProductCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// SalesUpdateDTO.java
@Data
public class SalesUpdateDTO {
    private LocalDateTime invoiceDate;
    private String deliveryToken;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private LocalDateTime returnValidUntil;
    private Boolean isReturned;
    private Long customerId;
    private Long salesPersonId;
    private Long inventoryId;
    private List<SalesItemDTO> salesItems;
    
    @Data
    public static class SalesItemDTO {
        private Long id;
        private Long productId;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private ProductCategory productCategory;
        private BigDecimal deliveredQuantity;
        private BigDecimal pendingQuantity;
    }
}