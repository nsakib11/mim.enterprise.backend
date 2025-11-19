package com.example.mim.enterprise.repository;

import com.example.mim.enterprise.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCode(String code);
    List<Product> findBySupplierId(Long supplierId);
}
