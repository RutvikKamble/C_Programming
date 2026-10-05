package com.example.assets_management.asset_technician.service;
import com.example.assets_management.asset_technician.entity.Technician;

import com.example.assets_management.asset_technician.dto.request.BulkDeleteRequest;
import com.example.assets_management.asset_technician.dto.request.BulkTechnicianUpdateRequest;
import com.example.assets_management.asset_technician.dto.request.TechnicianPatchDTO;

import java.util.List;

public interface TechnicianService {

    Technician createTechnician(Technician technician);

    Technician updateTechnician(Long id, Technician technician);

    Technician patchTechnician(Long id, TechnicianPatchDTO patchDTO);

    List<Technician> bulkUpdateTechnicians(BulkTechnicianUpdateRequest request);

    void deleteTechnician(Long id);

    void bulkDeleteTechnicians(BulkDeleteRequest request);

    Technician getTechnicianById(Long id);

    List<Technician> getAllTechnicians();

    List<Technician> getTechniciansByInternalFlag(Boolean isInternal);
}
