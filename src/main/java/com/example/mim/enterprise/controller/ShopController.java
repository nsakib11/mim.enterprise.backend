package com.example.mim.enterprise.controller;

import com.example.mim.enterprise.model.Shop;
import com.example.mim.enterprise.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    public Shop create(@RequestBody Shop shop) {
        return shopService.save(shop);
    }

    @PutMapping("/{id}")
    public Shop update(@PathVariable Long id, @RequestBody Shop shop) {
        return shopService.update(id, shop);
    }

    @GetMapping("/{id}")
    public Shop getOne(@PathVariable Long id) {
        return shopService.getById(id);
    }

    @GetMapping
    public List<Shop> getAll() {
        return shopService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        shopService.delete(id);
    }
}
