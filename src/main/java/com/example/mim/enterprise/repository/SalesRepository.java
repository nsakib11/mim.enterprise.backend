package com.example.mim.enterprise.repository;

import com.example.mim.enterprise.model.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SalesRepository extends JpaRepository<Sales, Long> {
    @Query("SELECT s.invoiceNo FROM Sales s ORDER BY s.id DESC LIMIT 1")
    String findLastInvoiceNo();

}
