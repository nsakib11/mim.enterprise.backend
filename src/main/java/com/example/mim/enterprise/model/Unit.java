// Unit.java
package com.example.mim.enterprise.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "units")
@Getter @Setter @NoArgsConstructor
public class Unit extends BaseEntity {

    @Column(unique = true)
    private String name;

    private String nameBn;
    
    private boolean active = true;
}