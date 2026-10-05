package com.example.assets_management.role.repository;

import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role.enums.RoleType;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface RoleRepository extends JpaRepository<Role,UUID> {

    List<Role> findByCompany_IdOrCompanyIsNull(UUID companyId);

    /** Built-in (global) roles are found by name, falling back to code for rows seeded by older scripts. */
    Optional<Role> findFirstByTypeAndNameIgnoreCase(RoleType type, String name);

    Optional<Role> findFirstByTypeAndCodeIgnoreCase(RoleType type, String code);
}
