package com.example.assets_management.branch.repository;
import com.example.assets_management.branch.entity.Branch;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
}