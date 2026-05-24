package com.example.Backend.controller;

import com.example.Backend.dto.WarehouseDtos.PartnerDto;
import com.example.Backend.service.PartnerCrudService;
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
@RequestMapping("/api/doitac")
public class PartnerController {
    private final PartnerCrudService partnerCrudService;

    public PartnerController(PartnerCrudService partnerCrudService) {
        this.partnerCrudService = partnerCrudService;
    }

    @GetMapping
    public List<PartnerDto> getPartners(@RequestParam(required = false) String loaiDtIn) {
        return partnerCrudService.getPartners(loaiDtIn);
    }

    @GetMapping("/{maDt}")
    public PartnerDto getPartner(@PathVariable String maDt) {
        return partnerCrudService.getPartner(maDt);
    }

    @PostMapping
    public PartnerDto createPartner(@RequestBody PartnerDto payload) {
        return partnerCrudService.createPartner(payload);
    }

    @PutMapping("/{maDt}")
    public PartnerDto updatePartner(@PathVariable String maDt, @RequestBody PartnerDto payload) {
        return partnerCrudService.updatePartner(maDt, payload);
    }

    @DeleteMapping("/{maDt}")
    public void deletePartner(@PathVariable String maDt) {
        partnerCrudService.deletePartner(maDt);
    }
}
