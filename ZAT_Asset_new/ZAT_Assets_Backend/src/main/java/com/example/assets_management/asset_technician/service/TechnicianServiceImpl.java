package com.example.assets_management.asset_technician.service;
import com.example.assets_management.asset_technician.entity.Technician;
import com.example.assets_management.asset_technician.repository.TechnicianRepository;

import com.example.assets_management.asset_technician.dto.request.BulkDeleteRequest;
import com.example.assets_management.asset_technician.dto.request.BulkTechnicianUpdateRequest;
import com.example.assets_management.asset_technician.dto.request.TechnicianPatchDTO;
import com.example.assets_management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianRepository technicianRepository;

    public TechnicianServiceImpl(TechnicianRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    @Override
    public Technician createTechnician(Technician technician) {
        return technicianRepository.save(technician);
    }

    @Override
    public Technician updateTechnician(Long id, Technician technician) {
        Technician existing = getExistingTechnician(id);

        existing.setTechnicianName(technician.getTechnicianName());
        existing.setIsInternal(technician.getIsInternal());
        existing.setEmployeeRef(technician.getEmployeeRef());
        existing.setSpecialization(technician.getSpecialization());
        existing.setPhone(technician.getPhone());
        existing.setEmail(technician.getEmail());
        existing.setStatus(technician.getStatus());

        return technicianRepository.save(existing);
    }

    @Override
    public Technician patchTechnician(Long id, TechnicianPatchDTO patchDTO) {
        Technician existing = getExistingTechnician(id);

        // Only update fields that are present (nonÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Ëœnull)
        if (patchDTO.getTechnicianName() != null) {
            existing.setTechnicianName(patchDTO.getTechnicianName());
        }
        if (patchDTO.getIsInternal() != null) {
            existing.setIsInternal(patchDTO.getIsInternal());
        }
        if (patchDTO.getEmployeeRef() != null) {
            existing.setEmployeeRef(patchDTO.getEmployeeRef());
        }
        if (patchDTO.getSpecialization() != null) {
            existing.setSpecialization(patchDTO.getSpecialization());
        }
        if (patchDTO.getPhone() != null) {
            existing.setPhone(patchDTO.getPhone());
        }
        if (patchDTO.getEmail() != null) {
            existing.setEmail(patchDTO.getEmail());
        }
        if (patchDTO.getStatus() != null) {
            existing.setStatus(patchDTO.getStatus());
        }

        return technicianRepository.save(existing);
    }

    @Override
    public List<Technician> bulkUpdateTechnicians(BulkTechnicianUpdateRequest request) {
        List<Long> ids = request.getIds();
        TechnicianPatchDTO patch = request.getUpdateFields();

        List<Technician> updated = new ArrayList<>();
        for (Long id : ids) {
            Technician existing = getExistingTechnician(id);
            // Apply the same patch to each
            if (patch.getTechnicianName() != null) {
                existing.setTechnicianName(patch.getTechnicianName());
            }
            if (patch.getIsInternal() != null) {
                existing.setIsInternal(patch.getIsInternal());
            }
            if (patch.getEmployeeRef() != null) {
                existing.setEmployeeRef(patch.getEmployeeRef());
            }
            if (patch.getSpecialization() != null) {
                existing.setSpecialization(patch.getSpecialization());
            }
            if (patch.getPhone() != null) {
                existing.setPhone(patch.getPhone());
            }
            if (patch.getEmail() != null) {
                existing.setEmail(patch.getEmail());
            }
            if (patch.getStatus() != null) {
                existing.setStatus(patch.getStatus());
            }
            updated.add(technicianRepository.save(existing));
        }
        return updated;
    }

    @Override
    public void deleteTechnician(Long id) {
        if (!technicianRepository.existsById(id)) {
            throw new ResourceNotFoundException("Technician not found with id: " + id);
        }
        technicianRepository.deleteById(id);
    }

    @Override
    public void bulkDeleteTechnicians(BulkDeleteRequest request) {
        List<Long> ids = request.getIds();
        for (Long id : ids) {
            if (!technicianRepository.existsById(id)) {
                throw new ResourceNotFoundException("Technician not found with id: " + id);
            }
        }
        technicianRepository.deleteAllById(ids);
    }

    @Override
    public Technician getTechnicianById(Long id) {
        return getExistingTechnician(id);
    }

    @Override
    public List<Technician> getAllTechnicians() {
        return technicianRepository.findAll();
    }

    @Override
    public List<Technician> getTechniciansByInternalFlag(Boolean isInternal) {
        return technicianRepository.findByIsInternal(isInternal);
    }

    // Helper to avoid duplication
    private Technician getExistingTechnician(Long id) {
        return technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + id));
    }
}
