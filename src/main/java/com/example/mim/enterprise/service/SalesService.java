package com.example.mim.enterprise.service;

import com.example.mim.enterprise.dto.SalesDto;
import com.example.mim.enterprise.dto.SalesItemDto;
import com.example.mim.enterprise.dto.SalesUpdateDTO;
import com.example.mim.enterprise.model.*;
import com.example.mim.enterprise.repository.InventoryStockRepository;
import com.example.mim.enterprise.repository.SalesRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesService {

    private final SalesRepository salesRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final EntityManager entityManager;

    public String generateInvoiceNo() {
        String lastInvoice = salesRepository.findLastInvoiceNo();

        if (lastInvoice == null || lastInvoice.isEmpty()) {
            return "INV0001";
        }

        String numberPart = lastInvoice.replaceAll("\\D+", "");
        int next = Integer.parseInt(numberPart) + 1;
        return "INV" + String.format("%04d", next);
    }

    // ----------------- CREATE -----------------
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

        // Update inventory stock if items are delivered
        if (hasDeliveredItems(saved)) {
            updateInventoryStockForSales(saved);
        }

        return toDto(saved);
    }

    // ----------------- UPDATE -----------------
    public Sales update(Long id, SalesUpdateDTO salesDTO) {
        Sales existingSale = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        // Store old delivered quantities for stock adjustment
        List<SalesItem> oldItems = List.copyOf(existingSale.getSalesItems());

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

        Sales updatedSale = salesRepository.save(existingSale);

        // Update inventory stock if delivery quantities changed
        if (hasDeliveredItems(updatedSale)) {
            updateInventoryStockForSales(updatedSale);
        }

        return updatedSale;
    }

    // ----------------- INVENTORY STOCK UPDATE FOR SALES -----------------
    private void updateInventoryStockForSales(Sales sales) {
        if (sales.getInventory() == null) {
            System.err.println("Warning: No inventory specified for sales: " + sales.getId());
            return;
        }

        for (SalesItem item : sales.getSalesItems()) {
            // Skip if no delivery or no product
            if (item.getDeliveredQuantity() == null ||
                    item.getDeliveredQuantity().compareTo(BigDecimal.ZERO) <= 0 ||
                    item.getProduct() == null) {
                continue;
            }

            Long inventoryId = sales.getInventory().getId();
            Long productId = item.getProduct().getId();

            // Check available stock
            InventoryStock stock = inventoryStockRepository
                    .findByInventoryIdAndProductId(inventoryId, productId)
                    .orElseThrow(() -> new RuntimeException(
                            "No stock available for product " + productId +
                                    " in inventory " + inventoryId
                    ));

            // Validate sufficient stock
            if (stock.getQuantity().compareTo(item.getDeliveredQuantity()) < 0) {
                throw new RuntimeException(
                        "Insufficient stock for product " + item.getProduct().getName() +
                                ". Available: " + stock.getQuantity() +
                                ", Required: " + item.getDeliveredQuantity()
                );
            }

            // DEDUCT delivered quantity from stock
            BigDecimal newQuantity = stock.getQuantity().subtract(item.getDeliveredQuantity());
            stock.setQuantity(newQuantity);

            inventoryStockRepository.save(stock);
        }
    }
//
//    // ----------------- STOCK RETURN HANDLING (for returns/refunds) -----------------
//    public void returnSalesItems(Long salesId, List<SalesReturnItem> returnItems) {
//        Sales sales = salesRepository.findById(salesId)
//                .orElseThrow(() -> new RuntimeException("Sales not found"));
//
//        for (SalesReturnItem returnItem : returnItems) {
//            SalesItem salesItem = sales.getSalesItems().stream()
//                    .filter(item -> item.getId().equals(returnItem.getSalesItemId()))
//                    .findFirst()
//                    .orElseThrow(() -> new RuntimeException("Sales item not found"));
//
//            // Validate return quantity
//            if (returnItem.getReturnQuantity().compareTo(salesItem.getDeliveredQuantity()) > 0) {
//                throw new RuntimeException("Return quantity exceeds delivered quantity");
//            }
//
//            // Add returned quantity back to stock
//            if (sales.getInventory() != null && salesItem.getProduct() != null) {
//                Long inventoryId = sales.getInventory().getId();
//                Long productId = salesItem.getProduct().getId();
//
//                InventoryStock stock = inventoryStockRepository
//                        .findByInventoryIdAndProductId(inventoryId, productId)
//                        .orElseGet(() -> {
//                            InventoryStock newStock = new InventoryStock();
//                            newStock.setInventory(sales.getInventory());
//                            newStock.setProduct(salesItem.getProduct());
//                            newStock.setQuantity(BigDecimal.ZERO);
//                            return newStock;
//                        });
//
//                BigDecimal newQuantity = stock.getQuantity().add(returnItem.getReturnQuantity());
//                stock.setQuantity(newQuantity);
//
//                inventoryStockRepository.save(stock);
//            }
//
//            // Update sales item quantities
//            salesItem.setDeliveredQuantity(
//                    salesItem.getDeliveredQuantity().subtract(returnItem.getReturnQuantity())
//            );
//            salesItem.setPendingQuantity(
//                    salesItem.getPendingQuantity().add(returnItem.getReturnQuantity())
//            );
//        }
//
//        // Recalculate totals
//        calculateTotals(sales);
//        salesRepository.save(sales);
//    }

    // ----------------- HELPER METHODS -----------------
    private boolean hasDeliveredItems(Sales sales) {
        return sales.getSalesItems().stream()
                .anyMatch(item -> item.getDeliveredQuantity() != null &&
                        item.getDeliveredQuantity().compareTo(BigDecimal.ZERO) > 0);
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
        Sales sales = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales not found"));

        // If sales has delivered items, restore stock before deletion
        if (hasDeliveredItems(sales)) {
            restoreInventoryStock(sales);
        }

        salesRepository.deleteById(id);
    }

    private void restoreInventoryStock(Sales sales) {
        if (sales.getInventory() == null) return;

        for (SalesItem item : sales.getSalesItems()) {
            if (item.getDeliveredQuantity() != null &&
                    item.getDeliveredQuantity().compareTo(BigDecimal.ZERO) > 0 &&
                    item.getProduct() != null) {

                Long inventoryId = sales.getInventory().getId();
                Long productId = item.getProduct().getId();

                InventoryStock stock = inventoryStockRepository
                        .findByInventoryIdAndProductId(inventoryId, productId)
                        .orElseGet(() -> {
                            InventoryStock newStock = new InventoryStock();
                            newStock.setInventory(sales.getInventory());
                            newStock.setProduct(item.getProduct());
                            newStock.setQuantity(BigDecimal.ZERO);
                            return newStock;
                        });

                BigDecimal newQuantity = stock.getQuantity().add(item.getDeliveredQuantity());
                stock.setQuantity(newQuantity);

                inventoryStockRepository.save(stock);
            }
        }
    }

    // DTO CONVERTERS
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
                s.getSalesItems().stream().map(this::toItemDto).toList()
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