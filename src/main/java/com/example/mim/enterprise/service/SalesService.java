package com.example.mim.enterprise.service;

import com.example.mim.enterprise.dto.SalesDto;
import com.example.mim.enterprise.dto.SalesItemDto;
import com.example.mim.enterprise.dto.SalesUpdateDTO;
import com.example.mim.enterprise.model.*;
import com.example.mim.enterprise.repository.SalesRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalesService {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private EntityManager entityManager;

    public String generateInvoiceNo() {
        String lastInvoice = salesRepository.findLastInvoiceNo(); // e.g. "INV0005"

        if (lastInvoice == null || lastInvoice.isEmpty()) {
            return "INV0001";
        }

        // Extract digits only → "0005"
        String numberPart = lastInvoice.replaceAll("\\D+", "");

        int next = Integer.parseInt(numberPart) + 1;

        return "INV" + String.format("%04d", next);
    }

    // CREATE
    @Transactional
    public SalesDto create(Sales sales) {
        sales.setInvoiceNo(generateInvoiceNo());
        sales.setInvoiceDate(LocalDateTime.now());


        if (sales.getSalesItems() != null) {
            for (SalesItem item : sales.getSalesItems()) {
                item.setSales(sales);
            }
        }

        calculateTotals(sales);

        Sales saved = salesRepository.save(sales);

        return toDto(saved);
    }

    // UPDATE
    public Sales update(Long id, SalesUpdateDTO salesDTO) {
        Sales existingSale = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        // Update simple fields
        existingSale.setInvoiceDate(salesDTO.getInvoiceDate());
        existingSale.setDeliveryToken(salesDTO.getDeliveryToken());
        existingSale.setDeliveryAddress(salesDTO.getDeliveryAddress());
        existingSale.setTotalAmount(salesDTO.getTotalAmount());
        existingSale.setPaidAmount(salesDTO.getPaidAmount());
        existingSale.setDueAmount(salesDTO.getDueAmount());
        existingSale.setPaymentStatus(salesDTO.getPaymentStatus());
        existingSale.setPaymentMethod(salesDTO.getPaymentMethod());
        existingSale.setReturnValidUntil(salesDTO.getReturnValidUntil());
        existingSale.setIsReturned(salesDTO.getIsReturned());

        // Update relationships by ID
        if (salesDTO.getCustomerId() != null) {
            Customer customer = entityManager.getReference(Customer.class, salesDTO.getCustomerId());
            existingSale.setCustomer(customer);
        }

        if (salesDTO.getSalesPersonId() != null) {
            Employee salesPerson = entityManager.getReference(Employee.class, salesDTO.getSalesPersonId());
            existingSale.setSalesPerson(salesPerson);
        }

        if (salesDTO.getInventoryId() != null) {
            Inventory inventory = entityManager.getReference(Inventory.class, salesDTO.getInventoryId());
            existingSale.setInventory(inventory);
        }

        // Handle sales items
        existingSale.getSalesItems().clear();

        for (SalesUpdateDTO.SalesItemDTO itemDTO : salesDTO.getSalesItems()) {
            SalesItem item = new SalesItem();
            item.setSales(existingSale);

            if (itemDTO.getProductId() != null) {
                Product product = entityManager.getReference(Product.class, itemDTO.getProductId());
                item.setProduct(product);
            }

            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setTotalPrice(itemDTO.getTotalPrice());
            item.setProductCategory(itemDTO.getProductCategory());
            item.setDeliveredQuantity(itemDTO.getDeliveredQuantity());
            item.setPendingQuantity(itemDTO.getPendingQuantity());

            existingSale.getSalesItems().add(item);
        }

        return salesRepository.save(existingSale);
    }
    // GET BY ID
    public SalesDto getById(Long id) {
        Sales sales = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales not found"));
        return toDto(sales);
    }

    // GET ALL
    public List<SalesDto> getAll() {
        return salesRepository.findAll()
                .stream().map(this::toDto).toList();
    }

    // DELETE
    public void delete(Long id) {
        salesRepository.deleteById(id);
    }

    // DTO CONVERTERS ---------------------------

    private SalesItemDto toItemDto(SalesItem item) {
        return new SalesItemDto(
                item.getId(),
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getProduct() != null ? item.getProduct().getName() : null,
                item.getQuantity(),
                item.getDeliveredQuantity(),
                item.getPendingQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice(),
                item.getProductCategory() != null ? item.getProductCategory().name() : null
        );
    }

    private SalesDto toDto(Sales s) {
        return new SalesDto(
                s.getId(),
                s.getInvoiceNo(),
                s.getInvoiceDate(),
                s.getCustomer() != null ? s.getCustomer().getId() : null,
                s.getCustomer() != null ? s.getCustomer().getName() : null,
                s.getDeliveryToken(),
                s.getDeliveryAddress(),
                s.getDeliveryAddressBn(),
                s.getDeliveryStatus() != null ? s.getDeliveryStatus().name() : null,
                s.getTotalAmount(),
                s.getPaidAmount(),
                s.getDueAmount(),
                s.getPaymentStatus() != null ? s.getPaymentStatus().name() : null,
                s.getPaymentMethod() != null ? s.getPaymentMethod().name() : null,
                s.getSalesPerson() != null ? s.getSalesPerson().getId() : null,
                s.getSalesPerson() != null ? s.getSalesPerson().getName() : null,
                s.getInventory() != null ? s.getInventory().getId() : null,
                s.getInventory() != null ? s.getInventory().getName() : null,
                s.getReturnValidUntil(),
                s.getIsReturned(),
                s.getSalesItems()
                        .stream().map(this::toItemDto).toList()
        );
    }

    // CALCULATE TOTALS
    private void calculateTotals(Sales sales) {
        if (sales.getSalesItems() == null || sales.getSalesItems().isEmpty()) return;

        BigDecimal total = sales.getSalesItems().stream()
                .map(i -> i.getQuantity().multiply(i.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        sales.setTotalAmount(total);
        sales.setDueAmount(total.subtract(
                sales.getPaidAmount() != null ? sales.getPaidAmount() : BigDecimal.ZERO
        ));
    }
}
