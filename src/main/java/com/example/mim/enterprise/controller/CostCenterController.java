package com.example.mim.enterprise.controller;

import com.example.mim.enterprise.model.CostCenter;
import com.example.mim.enterprise.service.CostCenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cost-centers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CostCenterController {

    private final CostCenterService costCenterService;

    @PostMapping
    public CostCenter create(@RequestBody CostCenter costCenter) {
        return costCenterService.save(costCenter);
    }

    @PutMapping("/{id}")
    public CostCenter update(@PathVariable Long id, @RequestBody CostCenter costCenter) {
        return costCenterService.update(id, costCenter);
    }

    @GetMapping("/{id}")
    public CostCenter getOne(@PathVariable Long id) {
        return costCenterService.getById(id);
    }

    @GetMapping
    public List<CostCenter> getAll() {
        return costCenterService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        costCenterService.delete(id);
    }
}
