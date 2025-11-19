package com.example.mim.enterprise.repository;

import com.example.mim.enterprise.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    boolean existsByCode(String code);
}
