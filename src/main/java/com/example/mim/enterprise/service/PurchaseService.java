package com.example.mim.enterprise.service;

import com.example.mim.enterprise.dto.PurchaseDto;
import com.example.mim.enterprise.dto.PurchaseItemDto;
import com.example.mim.enterprise.model.Inventory;
import com.example.mim.enterprise.model.InventoryStock;
import com.example.mim.enterprise.model.Purchase;
import com.example.mim.enterprise.model.PurchaseItem;
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

    private void updateInventoryStock(Purchase purchase) {
        for (PurchaseItem item : purchase.getPurchaseItems()) {

            if (item.getDeliveredQuantity() == null
                    || item.getDeliveredQuantity().compareTo(BigDecimal.ZERO) == 0) {
                continue; // No delivery → No stock increase
            }

            Long inventoryId = item.getInventory().getId();
            Long productId = item.getProduct().getId();

            // Fetch existing stock or create new
            InventoryStock stock = inventoryStockRepository
                    .findByInventoryIdAndProductId(inventoryId, productId)
                    .orElseGet(() -> {
                        InventoryStock s = new InventoryStock();
                        s.setInventory(item.getInventory());
                        s.setProduct(item.getProduct());
                        s.setQuantity(BigDecimal.ZERO);
                        return s;
                    });

            // ADD delivered quantity
            BigDecimal newQty = stock.getQuantity().add(item.getDeliveredQuantity());
            stock.setQuantity(newQty);

            inventoryStockRepository.save(stock);
        }
    }


    private String generatePurchaseOrderNo() {
        String lastOrderNo = purchaseRepository.findLastOrderNo();

        if (lastOrderNo == null || lastOrderNo.isEmpty()) {
            return "PO-000001";
        }

        // Extract numeric part: "PO-000123" → 123
        int lastNumber = Integer.parseInt(lastOrderNo.replace("PO-", ""));
        int nextNumber = lastNumber + 1;

        return String.format("PO-%06d", nextNumber);
    }

    // ---------------- CREATE ----------------
    public PurchaseDto create(Purchase purchase) {

        // Auto-generate PO number if not provided
        if (purchase.getPurchaseOrderNo() == null || purchase.getPurchaseOrderNo().isBlank()) {
            purchase.setPurchaseOrderNo(generatePurchaseOrderNo());
        }

        // Link child items
        if (purchase.getPurchaseItems() != null) {
            for (PurchaseItem item : purchase.getPurchaseItems()) {
                item.setPurchase(purchase);
                // IMPORTANT: ensure inventory is attached
                if (item.getInventory() != null && item.getInventory().getId() != null) {
                    Inventory inv = inventoryRepository.findById(item.getInventory().getId())
                            .orElseThrow(() -> new RuntimeException("Invalid inventory"));
                    item.setInventory(inv);
                }
            }
        }

        calculateTotals(purchase);

        Purchase saved = purchaseRepository.save(purchase);
        // Update stock only if delivery happened
        if (saved.getTotalDeliveredQuantity().compareTo(BigDecimal.ZERO) > 0) {
            updateInventoryStock(saved);
        }
        return toDto(saved);
    }


    // ---------------- UPDATE ----------------
    public PurchaseDto update(Long id, Purchase updated) {
        Purchase existing = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        existing.setPurchaseOrderNo(updated.getPurchaseOrderNo());
        existing.setOrderDate(updated.getOrderDate());
        existing.setExpectedDeliveryDate(updated.getExpectedDeliveryDate());
        existing.setSupplier(updated.getSupplier());
        existing.setQuotationNo(updated.getQuotationNo());
        existing.setSupplierInvoiceNo(updated.getSupplierInvoiceNo());
        existing.setPaymentType(updated.getPaymentType());
        existing.setTotalAmount(updated.getTotalAmount());
        existing.setPaidAmount(updated.getPaidAmount());
        existing.setDueAmount(updated.getDueAmount());
        existing.setPaymentStatus(updated.getPaymentStatus());
        existing.setDeliveryStatus(updated.getDeliveryStatus());
        existing.setPriceLocked(updated.isPriceLocked());

        // Remove old items
        existing.getPurchaseItems().clear();

        // Add updated items
        if (updated.getPurchaseItems() != null) {
            for (PurchaseItem item : updated.getPurchaseItems()) {

                // Attach parent
                item.setPurchase(existing);

                // IMPORTANT: attach inventory
                if (item.getInventory() != null && item.getInventory().getId() != null) {
                    Inventory inv = inventoryRepository.findById(item.getInventory().getId())
                            .orElseThrow(() -> new RuntimeException("Invalid inventory"));
                    item.setInventory(inv);
                }

                existing.getPurchaseItems().add(item);
            }
        }


        calculateTotals(existing);

        Purchase saved = purchaseRepository.save(existing);

// Only update stock if delivered quantity increased
        if (saved.getTotalDeliveredQuantity().compareTo(BigDecimal.ZERO) > 0) {
            updateInventoryStock(saved);
        }

        return toDto(saved);
    }

    // ---------------- GET ONE ----------------
    public PurchaseDto getById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        return toDto(purchase);
    }

    // ---------------- GET ALL ----------------
    public List<PurchaseDto> getAll() {
        return purchaseRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ---------------- DELETE ----------------
    public void delete(Long id) {
        purchaseRepository.deleteById(id);
    }

    // ---------------- CALCULATE TOTALS ----------------
    private void calculateTotals(Purchase purchase) {
        if (purchase.getPurchaseItems() != null && !purchase.getPurchaseItems().isEmpty()) {
            BigDecimal totalOrdered = purchase.getPurchaseItems().stream()
                    .map(item -> item.getOrderedQuantity() != null ? item.getOrderedQuantity() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalDelivered = purchase.getPurchaseItems().stream()
                    .map(item -> item.getDeliveredQuantity() != null ? item.getDeliveredQuantity() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            purchase.setTotalOrderedQuantity(totalOrdered);
            purchase.setTotalDeliveredQuantity(totalDelivered);
            purchase.setTotalPendingQuantity(totalOrdered.subtract(totalDelivered));
        } else {
            purchase.setTotalOrderedQuantity(BigDecimal.ZERO);
            purchase.setTotalDeliveredQuantity(BigDecimal.ZERO);
            purchase.setTotalPendingQuantity(BigDecimal.ZERO);
        }
    }

    // ---------------- ENTITY → DTO ----------------
    private PurchaseItemDto toItemDto(PurchaseItem item) {
        return new PurchaseItemDto(
                item.getId(),
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getProduct() != null ? item.getProduct().getName() : null,
                item.getInventory()!=null ? item.getInventory().getId() : null,
                item.getOrderedQuantity(),
                item.getDeliveredQuantity(),
                item.getPendingQuantity(),
                item.getPurchasePrice(),
                item.getSalesPriceMin(),
                item.getSalesPriceMax(),
                item.getCurrentPurchasePrice(),
                item.getProductCategory() != null ? item.getProductCategory().name() : null
        );
    }

    private PurchaseDto toDto(Purchase p) {
        return new PurchaseDto(
                p.getId(),
                p.getPurchaseOrderNo(),
                p.getOrderDate(),
                p.getExpectedDeliveryDate(),
                p.getSupplier() != null ? p.getSupplier().getId() : null,
                p.getSupplier() != null ? p.getSupplier().getName() : null,
                p.getQuotationNo(),
                p.getSupplierInvoiceNo(),
                p.getPaymentType() != null ? p.getPaymentType().name() : null,
                p.getTotalAmount(),
                p.getPaidAmount(),
                p.getDueAmount(),
                p.getPaymentStatus() != null ? p.getPaymentStatus().name() : null,
                p.getDeliveryStatus() != null ? p.getDeliveryStatus().name() : null,
                p.getTotalOrderedQuantity(),
                p.getTotalDeliveredQuantity(),
                p.getTotalPendingQuantity(),
                p.isPriceLocked(),
                p.getPurchaseItems().stream()
                        .map(this::toItemDto)
                        .toList()
        );
    }
}
