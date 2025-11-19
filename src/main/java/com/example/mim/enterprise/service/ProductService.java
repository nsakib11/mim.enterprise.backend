package com.example.mim.enterprise.service;

import com.example.mim.enterprise.model.Product;
import com.example.mim.enterprise.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProductService {

    @Autowired
    private  ProductRepository productRepository;

    // Create
    public Product save(Product product) {
        if (productRepository.existsByCode(product.getCode())) {
            throw new RuntimeException("Product code already exists");
        }
        return productRepository.save(product);
    }

    // Update
    public Product update(Long id, Product updatedProduct) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setCode(updatedProduct.getCode());
        product.setName(updatedProduct.getName());
        product.setNameBn(updatedProduct.getNameBn());
        product.setCategory(updatedProduct.getCategory());
        product.setSupplier(updatedProduct.getSupplier());
        product.setUnit(updatedProduct.getUnit());
        product.setActive(updatedProduct.isActive());

        return productRepository.save(product);
    }

    // Get one
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // Get all
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    // Delete
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsBySupplierId(Long supplierId) {
        return productRepository.findBySupplierId(supplierId);
    }
}
