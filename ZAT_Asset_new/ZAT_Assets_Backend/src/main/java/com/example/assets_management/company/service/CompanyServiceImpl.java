package com.example.assets_management.company.service;
import com.example.assets_management.company.entity.Company;
import com.example.assets_management.company.repository.CompanyRepository;

import com.example.assets_management.security.context.CurrentUserService;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CurrentUserService currentUserService;

    @Override
    public List<Company> findAll() {
        UUID companyId = currentUserService.getCompanyId();
        return companyRepository.findAll().stream()
                .filter(c -> c.getId() != null && c.getId().equals(companyId))
                .toList();
    }

    @Override
    public Optional<Company> findById(UUID id) {
        if (!currentUserService.getCompanyId().equals(id)) {
            return Optional.empty();
        }
        return companyRepository.findById(id);
    }

    @Override
    public Company save(Company company) {
        // Ensure a new entity is created (ID will be generated)
        company.setId(null);
        return companyRepository.save(company);
    }

    @Override
    public Company update(UUID id, Company company) {

        if (!currentUserService.getCompanyId().equals(id)) {
            throw new AccessDeniedException("Company is outside the authenticated scope");
        }

        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));

        existing.setCompanyName(company.getCompanyName());
        existing.setLegalName(company.getLegalName());
        existing.setGstinTaxId(company.getGstinTaxId());

        return companyRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        if (!currentUserService.getCompanyId().equals(id)) {
            throw new AccessDeniedException("Company is outside the authenticated scope");
        }
        companyRepository.deleteById(id);
    }
}