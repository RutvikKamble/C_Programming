package com.example.assets_management.department.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.department.entity.Department;
import com.example.assets_management.department.service.DepartmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PreAuthorize("hasAuthority(\'Asset.Organization.Create\')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Department createDepartment(@RequestBody Department department) {
        return departmentService.createDepartment(department);
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.Update\')")
    @PutMapping("/{id}")
    public Department updateDepartment(@PathVariable UUID id,
                                       @RequestBody Department department) {
        return departmentService.updateDepartment(id, department);
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.View\')")
    @GetMapping("/{id}")
    public Department getDepartmentById(@PathVariable UUID id) {
        return departmentService.getDepartmentById(id);
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.View\')")
    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @PreAuthorize("hasAuthority(\'Asset.Organization.Delete\')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDepartment(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
    }
}