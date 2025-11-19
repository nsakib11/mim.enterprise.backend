package com.example.mim.enterprise.controller;

import com.example.mim.enterprise.model.Bank;
import com.example.mim.enterprise.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banks")
@CrossOrigin(origins = "*")
public class BankController {

    @Autowired
    private BankService bankService;

    @PostMapping
    public Bank create(@RequestBody Bank bank) {
        return bankService.create(bank);
    }

    @PutMapping("/{id}")
    public Bank update(@PathVariable Long id, @RequestBody Bank bank) {
        return bankService.update(id, bank);
    }

    @GetMapping("/{id}")
    public Bank getById(@PathVariable Long id) {
        return bankService.getById(id);
    }

    @GetMapping
    public List<Bank> getAll() {
        return bankService.getAll();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        bankService.delete(id);
        return "Bank deleted successfully";
    }
}
