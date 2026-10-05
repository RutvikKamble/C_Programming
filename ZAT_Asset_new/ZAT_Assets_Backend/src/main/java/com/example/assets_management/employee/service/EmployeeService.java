package com.example.assets_management.employee.service;
import com.example.assets_management.employee.entity.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    // Create Employee
    Employee createEmployee(Employee employee);

    // Get all employees
    List<Employee> getAllEmployees();

    // Get employee by ID
    Employee getEmployeeById(UUID id);

    // Update employee
    Employee updateEmployee(UUID id, Employee employee);

    // Delete employee
    void deleteEmployee(UUID id);
}
