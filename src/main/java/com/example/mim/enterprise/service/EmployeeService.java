package com.example.mim.enterprise.service;

import com.example.mim.enterprise.model.CostCenter;
import com.example.mim.enterprise.model.Employee;
import com.example.mim.enterprise.model.Shop;
import com.example.mim.enterprise.repository.CostCenterRepository;
import com.example.mim.enterprise.repository.EmployeeRepository;
import com.example.mim.enterprise.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ShopRepository shopRepository;
    private final CostCenterRepository costCenterRepository;

    // Create
    public Employee save(Employee employee) {

        if (employeeRepository.existsByCode(employee.getCode())) {
            throw new RuntimeException("Employee code already exists");
        }

        // Set shop if exists
        if (employee.getShop() != null && employee.getShop().getId() != null) {
            Shop shop = shopRepository.findById(employee.getShop().getId())
                    .orElseThrow(() -> new RuntimeException("Shop not found"));
            employee.setShop(shop);
        }

        // Set cost center if exists
        if (employee.getCostCenter() != null && employee.getCostCenter().getId() != null) {
            CostCenter costCenter = costCenterRepository.findById(employee.getCostCenter().getId())
                    .orElseThrow(() -> new RuntimeException("Cost center not found"));
            employee.setCostCenter(costCenter);
        }

        return employeeRepository.save(employee);
    }

    // Update
    public Employee update(Long id, Employee updatedEmployee) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setCode(updatedEmployee.getCode());
        employee.setName(updatedEmployee.getName());
        employee.setNameBn(updatedEmployee.getNameBn());
        employee.setEmployeeType(updatedEmployee.getEmployeeType());
        employee.setMobile(updatedEmployee.getMobile());
        employee.setEmail(updatedEmployee.getEmail());
        employee.setAddress(updatedEmployee.getAddress());
        employee.setNidNumber(updatedEmployee.getNidNumber());
        employee.setJoinDate(updatedEmployee.getJoinDate());
        employee.setDateOfBirth(updatedEmployee.getDateOfBirth());
        employee.setBasicSalary(updatedEmployee.getBasicSalary());
        employee.setCurrentSalary(updatedEmployee.getCurrentSalary());
        employee.setEmploymentStatus(updatedEmployee.getEmploymentStatus());
        employee.setActive(updatedEmployee.isActive());

        // Update shop
        if (updatedEmployee.getShop() != null && updatedEmployee.getShop().getId() != null) {
            Shop shop = shopRepository.findById(updatedEmployee.getShop().getId())
                    .orElseThrow(() -> new RuntimeException("Shop not found"));
            employee.setShop(shop);
        }

        // Update cost center
        if (updatedEmployee.getCostCenter() != null && updatedEmployee.getCostCenter().getId() != null) {
            CostCenter costCenter = costCenterRepository.findById(updatedEmployee.getCostCenter().getId())
                    .orElseThrow(() -> new RuntimeException("Cost center not found"));
            employee.setCostCenter(costCenter);
        }

        return employeeRepository.save(employee);
    }

    // Get one
    public Employee getById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    // Get all
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    // Delete
    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}
