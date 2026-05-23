package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.CategoryDto;
import com.example.Backend.dto.WarehouseDtos.EmployeeDto;
import com.example.Backend.dto.WarehouseDtos.PartnerDto;
import com.example.Backend.dto.WarehouseDtos.ProductDto;
import com.example.Backend.service.WarehouseService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CatalogController {
    private final WarehouseService warehouseService;

    public CatalogController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/loaisp")
    public List<CategoryDto> getCategories() {
        return warehouseService.getCategories();
    }

    @PostMapping("/loaisp")
    public CategoryDto createCategory(@RequestBody CategoryDto payload) {
        return warehouseService.createCategory(payload);
    }

    @PutMapping("/loaisp/{maLoai}")
    public CategoryDto updateCategory(@PathVariable String maLoai, @RequestBody CategoryDto payload) {
        return warehouseService.updateCategory(maLoai, payload);
    }

    @DeleteMapping("/loaisp/{maLoai}")
    public void deleteCategory(@PathVariable String maLoai) {
        warehouseService.deleteCategory(maLoai);
    }

    @GetMapping("/sanpham")
    public List<ProductDto> getProducts() {
        return warehouseService.getProducts();
    }

    @GetMapping("/sanpham/{maSp}")
    public ProductDto getProduct(@PathVariable String maSp) {
        return warehouseService.getProduct(maSp);
    }

    @PostMapping("/sanpham")
    public ProductDto createProduct(@RequestBody ProductDto payload) {
        return warehouseService.createProduct(payload);
    }

    @PutMapping("/sanpham/{maSp}")
    public ProductDto updateProduct(@PathVariable String maSp, @RequestBody ProductDto payload) {
        return warehouseService.updateProduct(maSp, payload);
    }

    @DeleteMapping("/sanpham/{maSp}")
    public void deleteProduct(@PathVariable String maSp) {
        warehouseService.deleteProduct(maSp);
    }

    @GetMapping("/doitac")
    public List<PartnerDto> getPartners(@RequestParam(required = false) String loaiDtIn) {
        return warehouseService.getPartners(loaiDtIn);
    }

    @PostMapping("/doitac")
    public PartnerDto createPartner(@RequestBody PartnerDto payload) {
        return warehouseService.createPartner(payload);
    }

    @PutMapping("/doitac/{maDt}")
    public PartnerDto updatePartner(@PathVariable String maDt, @RequestBody PartnerDto payload) {
        return warehouseService.updatePartner(maDt, payload);
    }

    @DeleteMapping("/doitac/{maDt}")
    public void deletePartner(@PathVariable String maDt) {
        warehouseService.deletePartner(maDt);
    }

    @GetMapping("/nhanvien")
    public List<EmployeeDto> getEmployees(@RequestParam(required = false) String loaiNvIn) {
        return warehouseService.getEmployees(loaiNvIn);
    }

    @PostMapping("/nhanvien")
    public EmployeeDto createEmployee(@RequestBody EmployeeDto payload) {
        return warehouseService.createEmployee(payload);
    }

    @PutMapping("/nhanvien/{maNv}")
    public EmployeeDto updateEmployee(@PathVariable String maNv, @RequestBody EmployeeDto payload) {
        return warehouseService.updateEmployee(maNv, payload);
    }

    @DeleteMapping("/nhanvien/{maNv}")
    public void deleteEmployee(@PathVariable String maNv) {
        warehouseService.deleteEmployee(maNv);
    }
}
