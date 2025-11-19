package com.example.mim.enterprise.service;

import com.example.mim.enterprise.dto.SalesDto;
import com.example.mim.enterprise.dto.SalesItemDto;
import com.example.mim.enterprise.model.*;
import com.example.mim.enterprise.repository.SalesRepository;
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
    public SalesDto update(Long id, Sales updated) {

        Sales existing = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales not found"));

        existing.setInvoiceNo(updated.getInvoiceNo());
        existing.setInvoiceDate(updated.getInvoiceDate());
        existing.setCustomer(updated.getCustomer());
        existing.setDeliveryToken(updated.getDeliveryToken());
        existing.setDeliveryAddress(updated.getDeliveryAddress());
        existing.setDeliveryAddressBn(updated.getDeliveryAddressBn());
        existing.setDeliveryStatus(updated.getDeliveryStatus());
        existing.setPaidAmount(updated.getPaidAmount());
        existing.setPaymentStatus(updated.getPaymentStatus());
        existing.setPaymentMethod(updated.getPaymentMethod());
        existing.setSalesPerson(updated.getSalesPerson());
        existing.setInventory(updated.getInventory());
        existing.setReturnValidUntil(updated.getReturnValidUntil());
        existing.setIsReturned(updated.getIsReturned());

        // Handle Sales Items
        existing.getSalesItems().clear();
        if (updated.getSalesItems() != null) {
            for (SalesItem item : updated.getSalesItems()) {
                item.setSales(existing);
                existing.getSalesItems().add(item);
            }
        }

        calculateTotals(existing);

        Sales saved = salesRepository.save(existing);

        return toDto(saved);
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
