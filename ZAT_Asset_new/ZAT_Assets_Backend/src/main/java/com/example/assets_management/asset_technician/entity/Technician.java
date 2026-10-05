package com.example.assets_management.asset_technician.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "asset_technician")
@Getter
@Setter
@NoArgsConstructor
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "technicianName is required")
    private String technicianName;

    // true = your own staff, false = vendor's staff
    @NotNull(message = "isInternal is required")
    @Column(nullable = false)
    private Boolean isInternal;

    // If internal, can later be linked to an HRMS employee id
    private String employeeRef;

    // Hardware / Networking / Electrical / Vehicle Mechanic, etc.
    private String specialization;

    private String phone;

    private String email;

    // Active / Inactive
    private String status;
}
