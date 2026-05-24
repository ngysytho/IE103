package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.AuthUserDto;
import com.example.Backend.dto.WarehouseDtos.ReceiptDto;
import com.example.Backend.service.DemoAuthService;
import com.example.Backend.service.ReceiptCrudService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/phieunhap")
public class ReceiptController {
    private final ReceiptCrudService receiptCrudService;
    private final DemoAuthService authService;

    public ReceiptController(ReceiptCrudService receiptCrudService, DemoAuthService authService) {
        this.receiptCrudService = receiptCrudService;
        this.authService = authService;
    }

    @GetMapping
    public List<ReceiptDto> getReceipts() {
        return receiptCrudService.getReceipts();
    }

    @GetMapping("/{maPn}")
    public ReceiptDto getReceipt(@PathVariable String maPn) {
        return receiptCrudService.getReceipt(maPn);
    }

    @PostMapping
    public ReceiptDto createReceipt(@RequestBody ReceiptDto payload, HttpServletRequest request) {
        AuthUserDto user = authService.requireUser(request);
        return receiptCrudService.createReceipt(payload, user.maNv());
    }
}
