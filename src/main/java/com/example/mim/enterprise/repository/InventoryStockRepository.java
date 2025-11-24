package com.example.mim.enterprise.repository;

import com.example.mim.enterprise.model.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {

    Optional<InventoryStock> findByInventoryIdAndProductId(Long inventoryId, Long productId);
}
