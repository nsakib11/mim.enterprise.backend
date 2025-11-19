package com.example.mim.enterprise.service;

import com.example.mim.enterprise.model.Shop;
import com.example.mim.enterprise.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    // Create
    public Shop save(Shop shop) {
        if (shopRepository.existsByCode(shop.getCode())) {
            throw new RuntimeException("Shop code already exists");
        }
        return shopRepository.save(shop);
    }

    // Update
    public Shop update(Long id, Shop updatedShop) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        shop.setCode(updatedShop.getCode());
        shop.setName(updatedShop.getName());
        shop.setNameBn(updatedShop.getNameBn());
        shop.setAddress(updatedShop.getAddress());
        shop.setMonthlySalesTarget(updatedShop.getMonthlySalesTarget());
        shop.setYearlySalesTarget(updatedShop.getYearlySalesTarget());
        shop.setShopRent(updatedShop.getShopRent());
        shop.setEntertainmentBudget(updatedShop.getEntertainmentBudget());
        shop.setPettyCashLimit(updatedShop.getPettyCashLimit());
        shop.setActive(updatedShop.isActive());

        return shopRepository.save(shop);
    }

    // Get one
    public Shop getById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
    }

    // Get all
    public List<Shop> getAll() {
        return shopRepository.findAll();
    }

    // Delete
    public void delete(Long id) {
        shopRepository.deleteById(id);
    }
}
