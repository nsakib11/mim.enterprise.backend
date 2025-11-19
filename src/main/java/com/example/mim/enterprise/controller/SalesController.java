package com.example.mim.enterprise.controller;

import com.example.mim.enterprise.dto.SalesDto;
import com.example.mim.enterprise.model.Sales;
import com.example.mim.enterprise.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SalesController {

    @Autowired
    private SalesService salesService;

    // CREATE
    @PostMapping
    public ResponseEntity<SalesDto> create(@RequestBody Sales sales) {
        return ResponseEntity.ok(salesService.create(sales));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<SalesDto> update(@PathVariable Long id, @RequestBody Sales sales) {
        return ResponseEntity.ok(salesService.update(id, sales));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<SalesDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(salesService.getById(id));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<SalesDto>> getAll() {
        return ResponseEntity.ok(salesService.getAll());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        salesService.delete(id);
        return ResponseEntity.ok("Sales deleted successfully!");
    }
}
