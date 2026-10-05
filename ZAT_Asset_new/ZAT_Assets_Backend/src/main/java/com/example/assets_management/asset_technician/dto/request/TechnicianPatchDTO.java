package com.example.assets_management.asset_technician.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TechnicianPatchDTO {

    @Size(min = 2, message = "technicianName must be at least 2 characters")
    private String technicianName;

    private Boolean isInternal;

    private String employeeRef;

    private String specialization;

    @Size(min = 10, max = 15, message = "phone must be between 10 and 15 characters")
    private String phone;

    @Email(message = "email must be valid")
    private String email;

    private String status;
}