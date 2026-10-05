package com.example.assets_management.company.repository;
import com.example.assets_management.company.entity.Company;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
}