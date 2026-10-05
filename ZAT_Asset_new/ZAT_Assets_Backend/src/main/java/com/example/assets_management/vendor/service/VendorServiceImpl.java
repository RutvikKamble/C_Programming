package com.example.assets_management.vendor.service;
import com.example.assets_management.vendor.entity.Vendor;
import com.example.assets_management.vendor.repository.VendorRepository;

import com.example.assets_management.company.entity.Company;
import com.example.assets_management.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final CompanyRepository companyRepository;

    @Override
    public List<Vendor> findAll() {
        return vendorRepository.findAll();
    }

    @Override
    public Optional<Vendor> findById(UUID id) {
        return vendorRepository.findById(id);
    }

    @Override
    public Vendor save(Vendor vendor) {

        Company company = companyRepository.findById(vendor.getCompany().getId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        vendor.setCompany(company);
        vendor.setId(null);

        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor update(UUID id, Vendor vendor) {

        Vendor existing = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (vendor.getCompany() != null) {

            Company company = companyRepository.findById(vendor.getCompany().getId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            existing.setCompany(company);
        }

        existing.setName(vendor.getName());
        existing.setContactEmail(vendor.getContactEmail());
        existing.setContactPhone(vendor.getContactPhone());
        existing.setAddress(vendor.getAddress());

        return vendorRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        vendorRepository.deleteById(id);
    }
}