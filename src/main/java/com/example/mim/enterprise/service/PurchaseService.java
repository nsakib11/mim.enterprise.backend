package com.example.mim.enterprise.service;

import com.example.mim.enterprise.dto.PurchaseDto;
import com.example.mim.enterprise.dto.PurchaseItemDto;
import com.example.mim.enterprise.model.*;
import com.example.mim.enterprise.model.enums.DeliveryStatus;
import com.example.mim.enterprise.model.enums.PaymentStatus;
import com.example.mim.enterprise.model.enums.PaymentType;
import com.example.mim.enterprise.model.enums.ProductCategory;
import com.example.mim.enterprise.repository.InventoryRepository;
import com.example.mim.enterprise.repository.InventoryStockRepository;
import com.example.mim.enterprise.repository.PurchaseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final InventoryRepository inventoryRepository;

    // ----------------- CREATE -----------------
    public PurchaseDto create(PurchaseDto dto) {
        Purchase entity = dtoToEntity(dto);

        // Auto-generate Purchase Order No
        if (entity.getPurchaseOrderNo() == null || entity.getPurchaseOrderNo().isBlank()) {
            entity.setPurchaseOrderNo(generatePurchaseOrderNo());
        }

        calculateTotals(entity);
        Purchase saved = purchaseRepository.save(entity);

        if (saved.getTotalDeliveredQuantity().compareTo(BigDecimal.ZERO) > 0) {
            updateInventoryStock(saved);
        }

        return entityToDto(saved);
    }

    // ----------------- UPDATE -----------------
    public PurchaseDto update(Long id, PurchaseDto dto) {
        Purchase existing = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        Purchase updated = dtoToEntity(dto);
        updated.setId(existing.getId());

        calculateTotals(updated);

        Purchase saved = purchaseRepository.save(updated);

        if (saved.getTotalDeliveredQuantity().compareTo(BigDecimal.ZERO) > 0) {
            updateInventoryStock(saved);
        }

        return entityToDto(saved);
    }

    // ----------------- GET ONE -----------------
    public PurchaseDto getById(Long id) {
        Purchase p = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        return entityToDto(p);
    }

    // ----------------- GET ALL -----------------
    public List<PurchaseDto> getAll() {
        return purchaseRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // ----------------- DELETE -----------------
    public void delete(Long id) {
        purchaseRepository.deleteById(id);
    }

    // ---------------- INVENTORY UPDATE ----------------
    private void updateInventoryStock(Purchase purchase) {
        for (PurchaseItem item : purchase.getPurchaseItems()) {

            if (item.getDeliveredQuantity() == null
                    || item.getDeliveredQuantity().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            Long invId = item.getInventory().getId();
            Long prodId = item.getProduct().getId();

            InventoryStock stock = inventoryStockRepository
                    .findByInventoryIdAndProductId(invId, prodId)
                    .orElseGet(() -> {
                        InventoryStock s = new InventoryStock();
                        s.setInventory(item.getInventory());
                        s.setProduct(item.getProduct());
                        s.setQuantity(BigDecimal.ZERO);
                        return s;
                    });

            stock.setQuantity(stock.getQuantity().add(item.getDeliveredQuantity()));
            inventoryStockRepository.save(stock);
        }
    }

    // ---------------- AUTO PO NUMBER ----------------
    private String generatePurchaseOrderNo() {
        String last = purchaseRepository.findLastOrderNo();
        if (last == null) return "PO-000001";

        int num = Integer.parseInt(last.replace("PO-", ""));
        return String.format("PO-%06d", num + 1);
    }

    // ---------------- CALCULATE TOTALS ----------------
    private void calculateTotals(Purchase p) {
        if (p.getPurchaseItems() == null || p.getPurchaseItems().isEmpty()) {
            p.setTotalOrderedQuantity(BigDecimal.ZERO);
            p.setTotalDeliveredQuantity(BigDecimal.ZERO);
            p.setTotalPendingQuantity(BigDecimal.ZERO);
            return;
        }

        BigDecimal totalOrdered = p.getPurchaseItems().stream()
                .map(i -> i.getOrderedQuantity() != null ? i.getOrderedQuantity() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDelivered = p.getPurchaseItems().stream()
                .map(i -> i.getDeliveredQuantity() != null ? i.getDeliveredQuantity() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        p.setTotalOrderedQuantity(totalOrdered);
        p.setTotalDeliveredQuantity(totalDelivered);
        p.setTotalPendingQuantity(totalOrdered.subtract(totalDelivered));
    }

    // ----------------------------------------------------------
    //                     DTO → ENTITY
    // ----------------------------------------------------------
    private Purchase dtoToEntity(PurchaseDto dto) {
        Purchase p = new Purchase();

        p.setPurchaseOrderNo(dto.getPurchaseOrderNo());
        p.setOrderDate(dto.getOrderDate());
        p.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());

        if (dto.getSupplierId() != null) {
            Supplier s = new Supplier();
            s.setId(dto.getSupplierId());
            p.setSupplier(s);
        }

        p.setQuotationNo(dto.getQuotationNo());
        p.setSupplierInvoiceNo(dto.getSupplierInvoiceNo());
        p.setPaymentType(dto.getPaymentType() != null ?
                Enum.valueOf(PaymentType.class, dto.getPaymentType()) : null);

        p.setTotalAmount(dto.getTotalAmount());
        p.setPaidAmount(dto.getPaidAmount());
        p.setDueAmount(dto.getDueAmount());

        p.setPaymentStatus(dto.getPaymentStatus() != null ?
                Enum.valueOf(PaymentStatus.class, dto.getPaymentStatus()) : null);

        p.setDeliveryStatus(dto.getDeliveryStatus() != null ?
                Enum.valueOf(DeliveryStatus.class, dto.getDeliveryStatus()) : null);

        p.setPriceLocked(dto.isPriceLocked());

        // ----- Items -----
        if (dto.getPurchaseItems() != null) {
            for (PurchaseItemDto d : dto.getPurchaseItems()) {
                PurchaseItem item = new PurchaseItem();
                item.setPurchase(p);

                Product prod = new Product();
                prod.setId(d.getProductId());
                item.setProduct(prod);

                if (d.getInventoryId() != null) {
                    Inventory inv = inventoryRepository.findById(d.getInventoryId())
                            .orElseThrow(() -> new RuntimeException("Invalid inventory"));
                    item.setInventory(inv);
                }

                item.setOrderedQuantity(d.getOrderedQuantity());
                item.setDeliveredQuantity(d.getDeliveredQuantity());
                item.setPendingQuantity(d.getPendingQuantity());
                item.setPurchasePrice(d.getPurchasePrice());
                item.setSalesPriceMin(d.getSalesPriceMin());
                item.setSalesPriceMax(d.getSalesPriceMax());
                item.setCurrentPurchasePrice(d.getCurrentPurchasePrice());

                if (d.getProductCategory() != null) {
                    item.setProductCategory(
                            Enum.valueOf(ProductCategory.class, d.getProductCategory())
                    );
                }

                p.getPurchaseItems().add(item);
            }
        }

        return p;
    }

    // ----------------------------------------------------------
    //                     ENTITY → DTO
    // ----------------------------------------------------------
    private PurchaseDto entityToDto(Purchase p) {
        PurchaseDto dto = new PurchaseDto();

        dto.setId(p.getId());
        dto.setPurchaseOrderNo(p.getPurchaseOrderNo());
        dto.setOrderDate(p.getOrderDate());
        dto.setExpectedDeliveryDate(p.getExpectedDeliveryDate());

        if (p.getSupplier() != null) {
            dto.setSupplierId(p.getSupplier().getId());
            dto.setSupplierName(p.getSupplier().getName());
        }

        dto.setQuotationNo(p.getQuotationNo());
        dto.setSupplierInvoiceNo(p.getSupplierInvoiceNo());
        dto.setPaymentType(p.getPaymentType() != null ? p.getPaymentType().name() : null);
        dto.setTotalAmount(p.getTotalAmount());
        dto.setPaidAmount(p.getPaidAmount());
        dto.setDueAmount(p.getDueAmount());
        dto.setPaymentStatus(p.getPaymentStatus() != null ? p.getPaymentStatus().name() : null);
        dto.setDeliveryStatus(p.getDeliveryStatus() != null ? p.getDeliveryStatus().name() : null);
        dto.setTotalOrderedQuantity(p.getTotalOrderedQuantity());
        dto.setTotalDeliveredQuantity(p.getTotalDeliveredQuantity());
        dto.setTotalPendingQuantity(p.getTotalPendingQuantity());
        dto.setPriceLocked(p.isPriceLocked());

        dto.setPurchaseItems(
                p.getPurchaseItems().stream().map(this::entityToItemDto).toList()
        );

        return dto;
    }

    private PurchaseItemDto entityToItemDto(PurchaseItem i) {
        PurchaseItemDto dto = new PurchaseItemDto();

        dto.setId(i.getId());
        dto.setProductId(i.getProduct() != null ? i.getProduct().getId() : null);
        dto.setProductName(i.getProduct() != null ? i.getProduct().getName() : null);
        dto.setInventoryId(i.getInventory() != null ? i.getInventory().getId() : null);

        dto.setOrderedQuantity(i.getOrderedQuantity());
        dto.setDeliveredQuantity(i.getDeliveredQuantity());
        dto.setPendingQuantity(i.getPendingQuantity());
        dto.setPurchasePrice(i.getPurchasePrice());
        dto.setSalesPriceMin(i.getSalesPriceMin());
        dto.setSalesPriceMax(i.getSalesPriceMax());
        dto.setCurrentPurchasePrice(i.getCurrentPurchasePrice());
        dto.setProductCategory(i.getProductCategory() != null ?
                i.getProductCategory().name() : null);

        return dto;
    }
}
