package com.example.mim.enterprise.controller;

import com.example.mim.enterprise.model.Supplier;
import com.example.mim.enterprise.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {

    @Autowired
    private  SupplierService supplierService;

    @PostMapping
    public Supplier create(@RequestBody Supplier supplier) {
        return supplierService.create(supplier);
    }

    @PutMapping("/{id}")
    public Supplier update(@PathVariable Long id, @RequestBody Supplier supplier) {
        return supplierService.update(id, supplier);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        supplierService.delete(id);
        return "Supplier deleted";
    }

    @GetMapping("/{id}")
    public Supplier getOne(@PathVariable Long id) {
        return supplierService.getById(id);
    }

    @GetMapping
    public List<Supplier> getAll() {
        return supplierService.getAll();
    }
}
