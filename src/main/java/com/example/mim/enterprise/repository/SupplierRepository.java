package com.example.mim.enterprise.repository;

import com.example.mim.enterprise.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    boolean existsByCode(String code);
}
