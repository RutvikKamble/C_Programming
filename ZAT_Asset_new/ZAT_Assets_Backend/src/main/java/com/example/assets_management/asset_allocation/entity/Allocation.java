package com.example.assets_management.asset_allocation.entity;

import com.example.assets_management.asset_allocation.enums.AllocationStatus;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.department.entity.Department;
import com.example.assets_management.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "asset_allocations")
public class Allocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // FK -> Asset
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    // FK -> Employee (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Denormalized Department
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // Denormalized Branch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(name = "allocated_on", nullable = false)
    private LocalDate allocatedOn;

    @Column(name = "returned_on")
    private LocalDate returnedOn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllocationStatus status;

    @Column(name = "acknowledged_by_employee")
    private Boolean acknowledgedByEmployee;
}