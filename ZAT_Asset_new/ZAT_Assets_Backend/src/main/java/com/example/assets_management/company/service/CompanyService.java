package com.example.assets_management.company.service;
import com.example.assets_management.company.entity.Company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyService {

    List<Company> findAll();

    Optional<Company> findById(UUID id);

    Company save(Company company);

    Company update(UUID id, Company company);

    void delete(UUID id);
}