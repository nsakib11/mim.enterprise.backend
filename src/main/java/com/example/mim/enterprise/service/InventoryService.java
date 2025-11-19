package com.example.mim.enterprise.service;

import com.example.mim.enterprise.model.Inventory;
import com.example.mim.enterprise.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // Create
    public Inventory save(Inventory inventory) {
        if (inventoryRepository.existsByCode(inventory.getCode())) {
            throw new RuntimeException("Inventory code already exists");
        }
        return inventoryRepository.save(inventory);
    }

    // Update
    public Inventory update(Long id, Inventory updatedInventory) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.setCode(updatedInventory.getCode());
        inventory.setName(updatedInventory.getName());
        inventory.setNameBn(updatedInventory.getNameBn());
        inventory.setAddress(updatedInventory.getAddress());
        inventory.setResponsiblePerson(updatedInventory.getResponsiblePerson());
        inventory.setMobile(updatedInventory.getMobile());
        inventory.setActive(updatedInventory.isActive());

        return inventoryRepository.save(inventory);
    }

    // Get one
    public Inventory getById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    // Get all
    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }

    // Delete
    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }
}
