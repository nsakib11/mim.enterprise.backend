// Employee.java
package com.example.mim.enterprise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter @Setter @NoArgsConstructor
public class Employee extends BaseEntity {

    @Column(unique = true)
    private String code;

    private String name;
    private String nameBn;

    @Enumerated(EnumType.STRING)
    private EmployeeType employeeType; // SALES_PERSON, DRIVER, LABOR, OFFICER, MANAGER

    private String mobile;
    private String email;
    private String address;

    private String nidNumber; // National ID
    private LocalDate joinDate;
    private LocalDate dateOfBirth;

    // Salary Information
    private BigDecimal basicSalary;
    private BigDecimal currentSalary;

    // Shop Assignment (for sales persons, drivers, labors)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Shop shop;

    // Cost Center for accounting
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cost_center_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CostCenter costCenter;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus; // ACTIVE, INACTIVE, TERMINATED

    private boolean active = true;

}