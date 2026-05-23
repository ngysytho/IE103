package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.AuthUserDto;
import com.example.Backend.dto.WarehouseDtos.IssueDto;
import com.example.Backend.dto.WarehouseDtos.ReceiptDto;
import com.example.Backend.dto.WarehouseDtos.StocktakeDto;
import com.example.Backend.exception.ApiException;
import com.example.Backend.service.DemoAuthService;
import com.example.Backend.service.WarehouseService;
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
@RequestMapping("/api")
public class TransactionController {
    private final WarehouseService warehouseService;
    private final DemoAuthService authService;

    public TransactionController(WarehouseService warehouseService, DemoAuthService authService) {
        this.warehouseService = warehouseService;
        this.authService = authService;
    }

    @GetMapping("/phieunhap")
    public List<ReceiptDto> getReceipts() {
        return warehouseService.getReceipts();
    }

    @GetMapping("/phieunhap/{maPn}")
    public ReceiptDto getReceipt(@PathVariable String maPn) {
        return warehouseService.getReceipt(maPn);
    }

    @PostMapping("/phieunhap")
    public ReceiptDto createReceipt(@RequestBody ReceiptDto payload, HttpServletRequest request) {
        AuthUserDto user = authService.requireUser(request);
        return warehouseService.createReceipt(payload, user.maNv());
    }

    @GetMapping("/phieuxuat")
    public List<IssueDto> getIssues() {
        return warehouseService.getIssues();
    }

    @GetMapping("/phieuxuat/{maPx}")
    public IssueDto getIssue(@PathVariable String maPx) {
        return warehouseService.getIssue(maPx);
    }

    @PostMapping("/phieuxuat")
    public IssueDto createIssue(@RequestBody IssueDto payload, HttpServletRequest request) {
        AuthUserDto user = authService.requireUser(request);
        return warehouseService.createIssue(payload, user.maNv());
    }

    @GetMapping("/phieukiemke")
    public List<StocktakeDto> getStocktakes() {
        return warehouseService.getStocktakes();
    }

    @GetMapping("/phieukiemke/{maPkk}")
    public StocktakeDto getStocktake(@PathVariable String maPkk) {
        return warehouseService.getStocktake(maPkk);
    }

    @PostMapping("/phieukiemke")
    public StocktakeDto createStocktake(@RequestBody StocktakeDto payload, HttpServletRequest request) {
        AuthUserDto user = authService.requireUser(request);
        return warehouseService.createStocktake(payload, user.maNv());
    }

    @PatchMapping("/phieukiemke/{maPkk}/approve")
    public StocktakeDto approveStocktake(@PathVariable String maPkk, HttpServletRequest request) {
        AuthUserDto user = authService.requireUser(request);
        if (user.loaiNv() != 0) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Chỉ Quản lý kho được duyệt phiếu kiểm kê!");
        }
        return warehouseService.approveStocktake(maPkk);
    }
}
