package com.example.mim.enterprise.service;

import com.example.mim.enterprise.model.Bank;
import com.example.mim.enterprise.model.BankBranch;
import com.example.mim.enterprise.repository.BankBranchRepository;
import com.example.mim.enterprise.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankBranchService {

    @Autowired
    private BankBranchRepository branchRepo;

    @Autowired
    private BankRepository bankRepo;

    // Create
    public BankBranch create(BankBranch branch) {
        // Validate bank
        Bank bank = bankRepo.findById(branch.getBank().getId())
                .orElseThrow(() -> new RuntimeException("Bank not found"));
        branch.setBank(bank);

        return branchRepo.save(branch);
    }

    // Update
    public BankBranch update(Long id, BankBranch branch) {
        BankBranch existing = branchRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        existing.setCode(branch.getCode());
        existing.setName(branch.getName());
        existing.setNameBn(branch.getNameBn());
        existing.setAddress(branch.getAddress());
        existing.setContactPersonName(branch.getContactPersonName());
        existing.setMobile(branch.getMobile());
        existing.setEmail(branch.getEmail());
        existing.setRoutingNo(branch.getRoutingNo());
        existing.setActive(branch.isActive());

        // Update bank
        if (branch.getBank() != null) {
            Bank bank = bankRepo.findById(branch.getBank().getId())
                    .orElseThrow(() -> new RuntimeException("Bank not found"));
            existing.setBank(bank);
        }

        return branchRepo.save(existing);
    }

    // Get one
    public BankBranch getById(Long id) {
        return branchRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
    }

    // Get all
    public List<BankBranch> getAll() {
        return branchRepo.findAll();
    }

    // Delete
    public void delete(Long id) {
        branchRepo.deleteById(id);
    }
}
