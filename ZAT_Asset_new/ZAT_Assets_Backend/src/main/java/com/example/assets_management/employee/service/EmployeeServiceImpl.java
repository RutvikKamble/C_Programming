package com.example.assets_management.employee.service;

import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.enums.UserStatus;
import com.example.assets_management.employee.repository.EmployeeRepository;

import com.example.assets_management.company.entity.Company;
import com.example.assets_management.company.repository.CompanyRepository;

import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.branch.repository.BranchRepository;

import com.example.assets_management.department.entity.Department;
import com.example.assets_management.department.repository.DepartmentRepository;

import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role.enums.RoleStatus;
import com.example.assets_management.role.repository.RoleRepository;

import com.example.assets_management.user_permission.service.UserPermissionSyncService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final UserPermissionSyncService permissionSyncService;
    private final com.example.assets_management.security.rbac.service.RbacGuard rbacGuard;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Employee createEmployee(Employee employee) {

        if (employee.getCompany() == null || employee.getCompany().getId() == null) {
            throw new RuntimeException("Company ID must be provided");
        }

        UUID companyId = employee.getCompany().getId();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new RuntimeException("Company not found with id: " + companyId));

        employee.setCompany(company);

        // A role supplied on creation is validated and its permissions
        // are copied onto the new user's UUID.
        Role role = null;

        if (employee.getRole() != null && employee.getRole().getId() != null) {

            UUID roleId = employee.getRole().getId();

            role = roleRepository.findById(roleId)
                    .orElseThrow(() ->
                            new RuntimeException("Role not found with id: " + roleId));

            if (role.getStatus() != RoleStatus.ACTIVE) {
                throw new RuntimeException("Inactive role cannot be assigned");
            }

            if (role.getCompany() != null
                    && !role.getCompany().getId().equals(companyId)) {

                throw new RuntimeException("Role belongs to another company");
            }
        }

        if (role != null) {
            rbacGuard.assertCanAssignRole(role, null);
        }

        employee.setRole(role);

        /*
         * Password handling
         *
         * Postman sends:
         *
         * "password": "Admin@123"
         *
         * The plain password is converted to a BCrypt hash.
         * Only the hash is stored in users.password_hash.
         */
        if (employee.getPassword() != null
                && !employee.getPassword().isBlank()) {

            employee.setPasswordHash(
                    passwordEncoder.encode(employee.getPassword())
            );
        }

        /*
         * If status is not supplied, create the employee as ACTIVE.
         */
        if (employee.getStatus() == null) {
            employee.setStatus(UserStatus.ACTIVE);
        }

        Employee saved = employeeRepository.save(employee);

        if (role != null) {
            permissionSyncService.assignFromRole(saved);
        }

        return saved;
    }

    // Get All Employees
    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Get Employee By ID
    @Override
    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with id: " + id));
    }

    // Update Employee
    @Override
    public Employee updateEmployee(UUID id, Employee updatedEmployee) {

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with id: " + id));

        // Update fields
        existingEmployee.setFullName(updatedEmployee.getFullName());
        existingEmployee.setPhone(updatedEmployee.getPhone());
        existingEmployee.setStatus(updatedEmployee.getStatus());

        // Check for duplicate email
        if (updatedEmployee.getEmail() != null
                && !updatedEmployee.getEmail().isEmpty()) {

            if (!updatedEmployee.getEmail()
                    .equals(existingEmployee.getEmail())) {

                employeeRepository.findByEmail(updatedEmployee.getEmail())
                        .ifPresent(emp -> {
                            throw new RuntimeException(
                                    "Email already exists: "
                                            + updatedEmployee.getEmail()
                            );
                        });

                existingEmployee.setEmail(updatedEmployee.getEmail());
            }
        }

        // Check for duplicate employee code
        if (updatedEmployee.getEmployeeCode() != null
                && !updatedEmployee.getEmployeeCode().isEmpty()) {

            if (!updatedEmployee.getEmployeeCode()
                    .equals(existingEmployee.getEmployeeCode())) {

                employeeRepository
                        .findByEmployeeCode(updatedEmployee.getEmployeeCode())
                        .ifPresent(emp -> {
                            throw new RuntimeException(
                                    "Employee code already exists: "
                                            + updatedEmployee.getEmployeeCode()
                            );
                        });

                existingEmployee.setEmployeeCode(
                        updatedEmployee.getEmployeeCode()
                );
            }
        }

        // Company
        if (updatedEmployee.getCompany() != null
                && updatedEmployee.getCompany().getId() != null) {

            UUID companyId = updatedEmployee.getCompany().getId();

            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Company not found with id: " + companyId
                            ));

            existingEmployee.setCompany(company);
        }

        // Branch
        if (updatedEmployee.getBranch() != null
                && updatedEmployee.getBranch().getId() != null) {

            UUID branchId = updatedEmployee.getBranch().getId();

            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Branch not found with id: " + branchId
                            ));

            existingEmployee.setBranch(branch);
        }

        // Department
        if (updatedEmployee.getDepartment() != null
                && updatedEmployee.getDepartment().getId() != null) {

            UUID departmentId = updatedEmployee.getDepartment().getId();

            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Department not found with id: " + departmentId
                            ));

            existingEmployee.setDepartment(department);
        }

        return employeeRepository.save(existingEmployee);
    }

    // Delete Employee
    @Override
    public void deleteEmployee(UUID id) {

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with id: " + id));

        permissionSyncService.clearUser(id);

        employeeRepository.delete(existingEmployee);
    }
}