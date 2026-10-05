package com.example.assets_management.employee.repository;
import com.example.assets_management.employee.entity.Employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByEmployeeCode(String employeeCode);
    long countByRole_Id(UUID roleId);
    java.util.List<Employee> findByRole_Id(UUID roleId);
}
