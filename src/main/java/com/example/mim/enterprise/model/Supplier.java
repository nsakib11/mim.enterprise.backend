// entities/Supplier.java
package com.example.mim.enterprise.model;

import com.example.mim.enterprise.model.enums.SupplierProduct;
import com.example.mim.enterprise.model.enums.SupplierType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Supplier extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "name_bn", nullable = false)
    private String nameBn;

    @Enumerated(EnumType.STRING)
    @Column(name = "supplier_product", nullable = false)
    private SupplierProduct supplierProduct;

    @Enumerated(EnumType.STRING)
    @Column(name = "supplier_type", nullable = false)
    private SupplierType supplierType;

    @Column(name = "responsible_person")
    private String responsiblePerson;

    private String mobile;

    private String telephone;

    private String email;

    private String website;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "is_active")
    private Boolean isActive = true;

}