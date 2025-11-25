package com.example.mim.enterprise.controller;

import com.example.mim.enterprise.dto.PurchaseDto;
import com.example.mim.enterprise.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    // CREATE
    @PostMapping
    public ResponseEntity<PurchaseDto> create(@RequestBody PurchaseDto dto) {
        return ResponseEntity.ok(purchaseService.create(dto));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseDto> update(@PathVariable Long id, @RequestBody PurchaseDto dto) {
        return ResponseEntity.ok(purchaseService.update(id, dto));
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getById(id));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<PurchaseDto>> getAll() {
        return ResponseEntity.ok(purchaseService.getAll());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        purchaseService.delete(id);
        return ResponseEntity.ok("Purchase deleted successfully!");
    }
}
