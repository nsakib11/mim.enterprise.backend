package com.example.mim.enterprise.controller;

import com.example.mim.enterprise.dto.PurchaseDto;
import com.example.mim.enterprise.model.Purchase;
import com.example.mim.enterprise.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    // CREATE
    @PostMapping
    public ResponseEntity<PurchaseDto> create(@RequestBody Purchase purchase) {
        PurchaseDto created = purchaseService.create(purchase);
        return ResponseEntity.ok(created);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseDto> update(@PathVariable Long id, @RequestBody Purchase purchase) {
        PurchaseDto updated = purchaseService.update(id, purchase);
        return ResponseEntity.ok(updated);
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDto> getById(@PathVariable Long id) {
        PurchaseDto dto = purchaseService.getById(id);
        return ResponseEntity.ok(dto);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<PurchaseDto>> getAll() {
        List<PurchaseDto> list = purchaseService.getAll();
        return ResponseEntity.ok(list);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        purchaseService.delete(id);
        return ResponseEntity.ok("Purchase deleted successfully!");
    }
}
