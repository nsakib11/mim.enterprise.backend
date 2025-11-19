// Shop.java
package com.example.mim.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "shops")
@Getter @Setter @NoArgsConstructor
public class Shop extends BaseEntity {

    @Column(unique = true)
    private String code;

    private String name;
    private String nameBn;
    private String address;

    // Sales Target
    private BigDecimal monthlySalesTarget;
    private BigDecimal yearlySalesTarget;

    // Shop specific costs
    private BigDecimal shopRent;
    private BigDecimal entertainmentBudget;
    private BigDecimal pettyCashLimit;

    private boolean active = true;
}