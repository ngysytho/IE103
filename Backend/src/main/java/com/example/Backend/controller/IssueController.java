package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.AuthUserDto;
import com.example.Backend.dto.WarehouseDtos.IssueDto;
import com.example.Backend.service.DemoAuthService;
import com.example.Backend.service.IssueCrudService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/phieuxuat")
public class IssueController {
    private final IssueCrudService issueCrudService;
    private final DemoAuthService authService;

    public IssueController(IssueCrudService issueCrudService, DemoAuthService authService) {
        this.issueCrudService = issueCrudService;
        this.authService = authService;
    }

    @GetMapping
    public List<IssueDto> getIssues() {
        return issueCrudService.getIssues();
    }

    @GetMapping("/{maPx}")
    public IssueDto getIssue(@PathVariable String maPx) {
        return issueCrudService.getIssue(maPx);
    }

    @PostMapping
    public IssueDto createIssue(@RequestBody IssueDto payload, HttpServletRequest request) {
        AuthUserDto user = authService.requireUser(request);
        return issueCrudService.createIssue(payload, user.maNv());
    }
}
