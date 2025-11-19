package com.example.mim.enterprise.service;

import com.example.mim.enterprise.model.Unit;
import com.example.mim.enterprise.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitService {

    @Autowired
    private  UnitRepository unitRepository;

    // Create
    public Unit save(Unit unit) {
        if (unitRepository.existsByName(unit.getName())) {
            throw new RuntimeException("Unit name already exists");
        }
        return unitRepository.save(unit);
    }

    // Update
    public Unit update(Long id, Unit updatedUnit) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        unit.setName(updatedUnit.getName());
        unit.setNameBn(updatedUnit.getNameBn());
        unit.setActive(updatedUnit.isActive());

        return unitRepository.save(unit);
    }

    // Get one
    public Unit getById(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
    }

    // Get all
    public List<Unit> getAll() {
        return unitRepository.findAll();
    }

    // Delete
    public void delete(Long id) {
        unitRepository.deleteById(id);
    }
}
