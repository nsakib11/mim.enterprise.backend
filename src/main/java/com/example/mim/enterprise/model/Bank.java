// Bank.java
package com.example.mim.enterprise.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "banks")
@Getter @Setter @NoArgsConstructor
public class Bank extends BaseEntity {

    @Column(unique = true)
    private String code;

    private String name;
    private String nameBn;

    @Column(length = 500)
    private String headOfficeAddress;

    private String website;
    private boolean active = true;
}