package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.ProductDto;
import com.example.Backend.service.ProductCrudService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sanpham")
public class ProductController {
    private final ProductCrudService productCrudService;

    public ProductController(ProductCrudService productCrudService) {
        this.productCrudService = productCrudService;
    }

    @GetMapping
    public List<ProductDto> getProducts() {
        return productCrudService.getProducts();
    }

    @GetMapping("/{maSp}")
    public ProductDto getProduct(@PathVariable String maSp) {
        return productCrudService.getProduct(maSp);
    }

    @PostMapping
    public ProductDto createProduct(@RequestBody ProductDto payload) {
        return productCrudService.createProduct(payload);
    }

    @PutMapping("/{maSp}")
    public ProductDto updateProduct(@PathVariable String maSp, @RequestBody ProductDto payload) {
        return productCrudService.updateProduct(maSp, payload);
    }

    @DeleteMapping("/{maSp}")
    public void deleteProduct(@PathVariable String maSp) {
        productCrudService.deleteProduct(maSp);
    }
}
