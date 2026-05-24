package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.EmployeeDto;
import com.example.Backend.service.EmployeeCrudService;
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
@RequestMapping("/api/nhanvien")
public class EmployeeController {
    private final EmployeeCrudService employeeCrudService;

    public EmployeeController(EmployeeCrudService employeeCrudService) {
        this.employeeCrudService = employeeCrudService;
    }

    @GetMapping
    public List<EmployeeDto> getEmployees(@RequestParam(required = false) String loaiNvIn) {
        return employeeCrudService.getEmployees(loaiNvIn);
    }

    @GetMapping("/{maNv}")
    public EmployeeDto getEmployee(@PathVariable String maNv) {
        return employeeCrudService.getEmployee(maNv);
    }

    @PostMapping
    public EmployeeDto createEmployee(@RequestBody EmployeeDto payload) {
        return employeeCrudService.createEmployee(payload);
    }

    @PutMapping("/{maNv}")
    public EmployeeDto updateEmployee(@PathVariable String maNv, @RequestBody EmployeeDto payload) {
        return employeeCrudService.updateEmployee(maNv, payload);
    }

    @DeleteMapping("/{maNv}")
    public void deleteEmployee(@PathVariable String maNv) {
        employeeCrudService.deleteEmployee(maNv);
    }
}
