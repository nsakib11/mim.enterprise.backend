package com.example.mim.enterprise.repository;

import com.example.mim.enterprise.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    boolean existsByCode(String code);
}
