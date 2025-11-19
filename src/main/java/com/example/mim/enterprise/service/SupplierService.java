package com.example.mim.enterprise.service;

import com.example.mim.enterprise.model.Supplier;
import com.example.mim.enterprise.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public Supplier create(Supplier supplier) {
        if (supplierRepository.existsByCode(supplier.getCode())) {
            throw new RuntimeException("Supplier code already exists: " + supplier.getCode());
        }
        return supplierRepository.save(supplier);
    }

    public Supplier update(Long id, Supplier updatedSupplier) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        existing.setCode(updatedSupplier.getCode());
        existing.setName(updatedSupplier.getName());
        existing.setNameBn(updatedSupplier.getNameBn());
        existing.setSupplierProduct(updatedSupplier.getSupplierProduct());
        existing.setSupplierType(updatedSupplier.getSupplierType());
        existing.setResponsiblePerson(updatedSupplier.getResponsiblePerson());
        existing.setMobile(updatedSupplier.getMobile());
        existing.setTelephone(updatedSupplier.getTelephone());
        existing.setEmail(updatedSupplier.getEmail());
        existing.setWebsite(updatedSupplier.getWebsite());
        existing.setAddress(updatedSupplier.getAddress());
        existing.setIsActive(updatedSupplier.getIsActive());

        return supplierRepository.save(existing);
    }

    public void delete(Long id) {
        supplierRepository.deleteById(id);
    }

    public Supplier getById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }
}
