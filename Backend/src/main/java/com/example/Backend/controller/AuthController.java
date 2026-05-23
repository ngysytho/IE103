package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.AuthUserDto;
import com.example.Backend.dto.WarehouseDtos.LoginRequest;
import com.example.Backend.dto.WarehouseDtos.RegisterRequest;
import com.example.Backend.service.DemoAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final DemoAuthService authService;

    public AuthController(DemoAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthUserDto login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public AuthUserDto register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping("/me")
    public AuthUserDto me(HttpServletRequest request) {
        return authService.getCurrentUser(request);
    }
}
