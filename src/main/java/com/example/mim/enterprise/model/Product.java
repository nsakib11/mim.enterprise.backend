// Product.java
package com.example.mim.enterprise.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor
public class Product extends BaseEntity{

    @Column(unique=true)
    private String code;

    private String name;
    private String nameBn;
    private String category;

    @ManyToOne
    @JoinColumn(name="supplier_id")

    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    private boolean active = true;
}
