package com.example.assets_management.branch.service;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.branch.repository.BranchRepository;

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
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;
    private final CurrentUserService currentUserService;

    @Override
    public List<Branch> findAll() {
        UUID companyId = currentUserService.getCompanyId();
        UUID branchId = currentUserService.getBranchId();
        return branchRepository.findAll().stream()
                .filter(b -> b.getCompany() != null && b.getCompany().getId().equals(companyId))
                .filter(b -> branchId == null || b.getId().equals(branchId))
                .toList();
    }

    @Override
    public Optional<Branch> findById(UUID id) {
        UUID branchId = currentUserService.getBranchId();
        return branchRepository.findById(id)
                .filter(b -> b.getCompany() != null
                        && b.getCompany().getId().equals(currentUserService.getCompanyId()))
                .filter(b -> branchId == null || b.getId().equals(branchId));
    }

    @Override
    public Branch save(Branch branch) {

        if (branch == null || branch.getCompany() == null || branch.getCompany().getId() == null) {
            throw new IllegalArgumentException("Branch company is required");
        }
        if (!currentUserService.getCompanyId().equals(branch.getCompany().getId())) {
            throw new AccessDeniedException("Branch company is outside the authenticated scope");
        }

        Company company = companyRepository.findById(branch.getCompany().getId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        branch.setId(null);
        branch.setCompany(company);

        return branchRepository.save(branch);
    }

    @Override
    public Branch update(UUID id, Branch branch) {

        Branch existing = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        if (!findById(id).isPresent()) {
            throw new AccessDeniedException("Branch is outside the authenticated scope");
        }

        if (branch.getCompany() != null) {

            Company company = companyRepository.findById(branch.getCompany().getId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            existing.setCompany(company);
        }

        existing.setBranchName(branch.getBranchName());
        existing.setAddress(branch.getAddress());
        existing.setCity(branch.getCity());
        existing.setState(branch.getState());
        existing.setCountry(branch.getCountry());

        return branchRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        if (!findById(id).isPresent()) {
            throw new AccessDeniedException("Branch is outside the authenticated scope");
        }
        branchRepository.deleteById(id);
    }
}