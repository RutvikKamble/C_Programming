package com.example.assets_management.department.service;
import com.example.assets_management.department.entity.Department;
import com.example.assets_management.department.repository.DepartmentRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(UUID id, Department department) {

        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));

        existingDepartment.setCompany(department.getCompany());
        existingDepartment.setBranch(department.getBranch());
        existingDepartment.setDepartmentName(department.getDepartmentName());

        return departmentRepository.save(existingDepartment);
    }

    @Override
    public Department getDepartmentById(UUID id) {

        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public void deleteDepartment(UUID id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));

        departmentRepository.delete(department);
    }
}