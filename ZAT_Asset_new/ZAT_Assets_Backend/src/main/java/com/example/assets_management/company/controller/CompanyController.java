package com.example.assets_management.company.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.company.entity.Company;
import com.example.assets_management.company.service.CompanyService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PreAuthorize("hasAuthority(\'Asset.Organization.View\')")
    @GetMapping
    public List<Company> getAllCompanies() {
        return companyService.findAll();
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable UUID id) {
        return companyService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.Create\')")
    @PostMapping
    public Company createCompany(@RequestBody Company company) {
        return companyService.save(company);
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable UUID id, @RequestBody Company company) {
        try {
            Company updated = companyService.update(id, company);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}