// Inventory.java
package com.example.mim.enterprise.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventories")
@Getter @Setter @NoArgsConstructor
public class Inventory extends BaseEntity {

    @Column(unique = true)
    private String code;

    private String name;
    private String nameBn;

    @Column(length = 500)
    private String address;

    private String responsiblePerson;
    private String mobile;
    private boolean active = true;
}