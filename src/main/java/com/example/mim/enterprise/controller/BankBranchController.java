package com.example.mim.enterprise.controller;

import com.example.mim.enterprise.model.BankBranch;
import com.example.mim.enterprise.service.BankBranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-branches")
@CrossOrigin(origins = "*")
public class BankBranchController {

    @Autowired
    private BankBranchService branchService;

    // Create
    @PostMapping
    public BankBranch create(@RequestBody BankBranch branch) {
        return branchService.create(branch);
    }

    // Update
    @PutMapping("/{id}")
    public BankBranch update(@PathVariable Long id, @RequestBody BankBranch branch) {
        return branchService.update(id, branch);
    }

    // Get One
    @GetMapping("/{id}")
    public BankBranch getById(@PathVariable Long id) {
        return branchService.getById(id);
    }

    // Get All
    @GetMapping
    public List<BankBranch> getAll() {
        return branchService.getAll();
    }

    // Delete
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        branchService.delete(id);
        return "Bank Branch deleted successfully";
    }
}
