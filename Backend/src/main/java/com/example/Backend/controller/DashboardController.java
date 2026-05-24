package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.DashboardSummaryDto;
import com.example.Backend.dto.WarehouseDtos.ImportByPartnerDto;
import com.example.Backend.dto.WarehouseDtos.LowStockDto;
import com.example.Backend.dto.WarehouseDtos.StocktakeDifferenceDto;
import com.example.Backend.service.DashboardQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {
    private final DashboardQueryService dashboardQueryService;

    public DashboardController(DashboardQueryService dashboardQueryService) {
        this.dashboardQueryService = dashboardQueryService;
    }

    @GetMapping("/warehouse/dashboard")
    public DashboardSummaryDto getDashboardSummary() {
        return dashboardQueryService.getDashboardSummary();
    }

    @GetMapping("/reports/low-stock")
    public List<LowStockDto> getLowStockReport() {
        return dashboardQueryService.getLowStockReport();
    }

    @GetMapping("/reports/import-by-partner")
    public List<ImportByPartnerDto> getImportByPartnerReport() {
        return dashboardQueryService.getImportByPartnerReport();
    }

    @GetMapping("/reports/stocktake-differences")
    public List<StocktakeDifferenceDto> getStocktakeDifferenceReport() {
        return dashboardQueryService.getStocktakeDifferenceReport();
    }
}
