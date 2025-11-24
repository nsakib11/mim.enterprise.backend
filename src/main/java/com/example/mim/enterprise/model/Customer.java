// Customer.java
package com.example.mim.enterprise.model;

import com.example.mim.enterprise.model.enums.CustomerType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor
public class Customer extends BaseEntity {

    @Column(unique = true)
    private String code;

    private String name;
    private String nameBn;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType; // INDIVIDUAL, PARTY

    private String mobile;
    private String email;

    @Column(length = 500)
    private String address;

    private boolean active = true;


}