package com.example.mim.enterprise.service;

import com.example.mim.enterprise.model.CostCenter;
import com.example.mim.enterprise.repository.CostCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CostCenterService {

    private final CostCenterRepository costCenterRepository;

    // Create
    public CostCenter save(CostCenter costCenter) {
        if (costCenterRepository.existsByCode(costCenter.getCode())) {
            throw new RuntimeException("Cost center code already exists");
        }
        return costCenterRepository.save(costCenter);
    }

    // Update
    public CostCenter update(Long id, CostCenter updatedCostCenter) {
        CostCenter costCenter = costCenterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cost center not found"));

        costCenter.setCode(updatedCostCenter.getCode());
        costCenter.setName(updatedCostCenter.getName());
        costCenter.setNameBn(updatedCostCenter.getNameBn());
        costCenter.setCostCenterType(updatedCostCenter.getCostCenterType());
        costCenter.setActive(updatedCostCenter.isActive());

        return costCenterRepository.save(costCenter);
    }

    // Get one
    public CostCenter getById(Long id) {
        return costCenterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cost center not found"));
    }

    // Get all
    public List<CostCenter> getAll() {
        return costCenterRepository.findAll();
    }

    // Delete
    public void delete(Long id) {
        costCenterRepository.deleteById(id);
    }
}
