package com.example.assets_management.vendor.service;
import com.example.assets_management.vendor.entity.Vendor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendorService {

    List<Vendor> findAll();

    Optional<Vendor> findById(UUID id);

    Vendor save(Vendor vendor);

    Vendor update(UUID id, Vendor vendor);

    void delete(UUID id);
}