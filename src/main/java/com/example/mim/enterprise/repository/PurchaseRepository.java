package com.example.mim.enterprise.repository;

import com.example.mim.enterprise.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @Query("SELECT p.purchaseOrderNo FROM Purchase p ORDER BY p.id DESC LIMIT 1")
    String findLastOrderNo();

}
