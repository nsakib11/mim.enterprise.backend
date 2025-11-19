package com.example.mim.enterprise.repository;

import com.example.mim.enterprise.model.CostCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostCenterRepository extends JpaRepository<CostCenter, Long> {

    boolean existsByCode(String code);
}
