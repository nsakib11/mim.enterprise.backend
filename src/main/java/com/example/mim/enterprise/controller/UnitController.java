package com.example.mim.enterprise.controller;

import com.example.mim.enterprise.model.Unit;
import com.example.mim.enterprise.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@CrossOrigin(origins = "*")
public class UnitController {

    @Autowired
    private  UnitService unitService;

    @PostMapping
    public Unit create(@RequestBody Unit unit) {
        return unitService.save(unit);
    }

    @PutMapping("/{id}")
    public Unit update(@PathVariable Long id, @RequestBody Unit unit) {
        return unitService.update(id, unit);
    }

    @GetMapping("/{id}")
    public Unit getOne(@PathVariable Long id) {
        return unitService.getById(id);
    }

    @GetMapping
    public List<Unit> getAll() {
        return unitService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        unitService.delete(id);
    }
}
