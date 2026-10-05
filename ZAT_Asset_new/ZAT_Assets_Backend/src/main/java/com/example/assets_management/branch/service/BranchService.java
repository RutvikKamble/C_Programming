package com.example.assets_management.branch.service;
import com.example.assets_management.branch.entity.Branch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchService {

    List<Branch> findAll();

    Optional<Branch> findById(UUID id);

    Branch save(Branch branch);

    Branch update(UUID id, Branch branch);

    void delete(UUID id);
}