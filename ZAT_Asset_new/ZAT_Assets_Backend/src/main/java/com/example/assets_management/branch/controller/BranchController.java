package com.example.assets_management.branch.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.branch.service.BranchService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PreAuthorize("hasAuthority(\'Asset.Organization.View\')")
    @GetMapping
    public List<Branch> getAllBranches() {
        return branchService.findAll();
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<Branch> getBranchById(@PathVariable UUID id) {

        return branchService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.Create\')")
    @PostMapping
    public Branch createBranch(@RequestBody Branch branch) {
        return branchService.save(branch);
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<Branch> updateBranch(@PathVariable UUID id,
                                               @RequestBody Branch branch) {

        try {
            return ResponseEntity.ok(branchService.update(id, branch));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable UUID id) {

        branchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}