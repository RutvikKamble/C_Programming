package com.example.assets_management.asset_technician.repository;
import com.example.assets_management.asset_technician.entity.Technician;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {

    // Keep the method for filtering by internal flag (used by the service)
    List<Technician> findByIsInternal(Boolean isInternal);

    // Optional ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Å“ you may keep this if still needed for other parts of the app
    List<Technician> findByStatus(String status);
}
