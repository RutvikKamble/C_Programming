package com.example.assets_management.department.service;
import com.example.assets_management.department.entity.Department;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {

    Department createDepartment(Department department);

    Department updateDepartment(UUID id, Department department);

    Department getDepartmentById(UUID id);

    List<Department> getAllDepartments();

    void deleteDepartment(UUID id);
}