// CostCenter.java
package com.example.mim.enterprise.model;

import com.example.mim.enterprise.model.enums.CostCenterType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cost_centers")
@Getter @Setter @NoArgsConstructor
public class CostCenter extends BaseEntity {

    @Column(unique = true)
    private String code;

    private String name;
    private String nameBn;

    @Enumerated(EnumType.STRING)
    private CostCenterType costCenterType; // SHOP, OFFICE, INVENTORY, GENERAL

    private boolean active = true;


}