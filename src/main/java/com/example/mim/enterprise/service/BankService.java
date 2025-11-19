package com.example.mim.enterprise.service;

import com.example.mim.enterprise.model.Bank;
import com.example.mim.enterprise.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {

    @Autowired
    private BankRepository bankRepository;

    // Create
    public Bank create(Bank bank) {
        return bankRepository.save(bank);
    }

    // Update
    public Bank update(Long id, Bank bank) {
        Bank existing = bankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank not found"));

        existing.setCode(bank.getCode());
        existing.setName(bank.getName());
        existing.setNameBn(bank.getNameBn());
        existing.setHeadOfficeAddress(bank.getHeadOfficeAddress());
        existing.setWebsite(bank.getWebsite());
        existing.setActive(bank.isActive());

        return bankRepository.save(existing);
    }

    // Get one
    public Bank getById(Long id) {
        return bankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank not found"));
    }

    // Get all
    public List<Bank> getAll() {
        return bankRepository.findAll();
    }

    // Delete
    public void delete(Long id) {
        bankRepository.deleteById(id);
    }
}
