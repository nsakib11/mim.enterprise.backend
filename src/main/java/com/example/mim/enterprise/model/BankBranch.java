// BankBranch.java
package com.example.mim.enterprise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_branches")
@Getter @Setter @NoArgsConstructor
public class BankBranch extends BaseEntity {

    @Column(unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "bank_id")
    private Bank bank;

    private String name;
    private String nameBn;

    @Column(length = 500)
    private String address;

    private String contactPersonName;
    private String mobile;
    private String email;

    @Column(name = "routing_no")
    private String routingNo;

    private boolean active = true;
}