package com.example.assets_management.vendor.repository;
import com.example.assets_management.vendor.entity.Vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {
}