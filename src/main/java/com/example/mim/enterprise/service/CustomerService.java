package com.example.mim.enterprise.service;

import com.example.mim.enterprise.model.Customer;
import com.example.mim.enterprise.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    // Create
    public Customer save(Customer customer) {
        if (customerRepository.existsByCode(customer.getCode())) {
            throw new RuntimeException("Customer code already exists");
        }
        return customerRepository.save(customer);
    }

    // Update
    public Customer update(Long id, Customer updatedCustomer) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setCode(updatedCustomer.getCode());
        customer.setName(updatedCustomer.getName());
        customer.setNameBn(updatedCustomer.getNameBn());
        customer.setCustomerType(updatedCustomer.getCustomerType());
        customer.setMobile(updatedCustomer.getMobile());
        customer.setEmail(updatedCustomer.getEmail());
        customer.setAddress(updatedCustomer.getAddress());
        customer.setActive(updatedCustomer.isActive());

        return customerRepository.save(customer);
    }

    // Get one
    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    // Get all
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    // Delete
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }
}
