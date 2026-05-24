package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.CategoryDto;
import com.example.Backend.service.CategoryCrudService;
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
@RequestMapping("/api/loaisp")
public class CategoryController {
    private final CategoryCrudService categoryCrudService;

    public CategoryController(CategoryCrudService categoryCrudService) {
        this.categoryCrudService = categoryCrudService;
    }

    @GetMapping
    public List<CategoryDto> getCategories() {
        return categoryCrudService.getCategories();
    }

    @GetMapping("/{maLoai}")
    public CategoryDto getCategory(@PathVariable String maLoai) {
        return categoryCrudService.getCategory(maLoai);
    }

    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryDto payload) {
        return categoryCrudService.createCategory(payload);
    }

    @PutMapping("/{maLoai}")
    public CategoryDto updateCategory(@PathVariable String maLoai, @RequestBody CategoryDto payload) {
        return categoryCrudService.updateCategory(maLoai, payload);
    }

    @DeleteMapping("/{maLoai}")
    public void deleteCategory(@PathVariable String maLoai) {
        categoryCrudService.deleteCategory(maLoai);
    }
}
