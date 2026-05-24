package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.AuthUserDto;
import com.example.Backend.dto.WarehouseDtos.StocktakeDto;
import com.example.Backend.exception.ApiException;
import com.example.Backend.service.DemoAuthService;
import com.example.Backend.service.StocktakeCrudService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/phieukiemke")
public class StocktakeController {
    private final StocktakeCrudService stocktakeCrudService;
    private final DemoAuthService authService;

    public StocktakeController(StocktakeCrudService stocktakeCrudService, DemoAuthService authService) {
        this.stocktakeCrudService = stocktakeCrudService;
        this.authService = authService;
    }

    @GetMapping
    public List<StocktakeDto> getStocktakes() {
        return stocktakeCrudService.getStocktakes();
    }

    @GetMapping("/{maPkk}")
    public StocktakeDto getStocktake(@PathVariable String maPkk) {
        return stocktakeCrudService.getStocktake(maPkk);
    }

    @PostMapping
    public StocktakeDto createStocktake(@RequestBody StocktakeDto payload, HttpServletRequest request) {
        AuthUserDto user = authService.requireUser(request);
        return stocktakeCrudService.createStocktake(payload, user.maNv());
    }

    @PatchMapping("/{maPkk}/approve")
    public StocktakeDto approveStocktake(@PathVariable String maPkk, HttpServletRequest request) {
        AuthUserDto user = authService.requireUser(request);
        if (user.loaiNv() != 0) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Chỉ Quản lý kho được duyệt phiếu kiểm kê!");
        }
        return stocktakeCrudService.approveStocktake(maPkk);
    }
}
