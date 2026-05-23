package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.DashboardSummaryDto;
import com.example.Backend.dto.WarehouseDtos.ImportByPartnerDto;
import com.example.Backend.dto.WarehouseDtos.LowStockDto;
import com.example.Backend.dto.WarehouseDtos.StocktakeDifferenceDto;
import com.example.Backend.service.WarehouseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {
    private final WarehouseService warehouseService;

    public DashboardController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/warehouse/dashboard")
    public DashboardSummaryDto getDashboardSummary() {
        return warehouseService.getDashboardSummary();
    }

    @GetMapping("/reports/low-stock")
    public List<LowStockDto> getLowStockReport() {
        return warehouseService.getLowStockReport();
    }

    @GetMapping("/reports/import-by-partner")
    public List<ImportByPartnerDto> getImportByPartnerReport() {
        return warehouseService.getImportByPartnerReport();
    }

    @GetMapping("/reports/stocktake-differences")
    public List<StocktakeDifferenceDto> getStocktakeDifferenceReport() {
        return warehouseService.getStocktakeDifferenceReport();
    }
}
