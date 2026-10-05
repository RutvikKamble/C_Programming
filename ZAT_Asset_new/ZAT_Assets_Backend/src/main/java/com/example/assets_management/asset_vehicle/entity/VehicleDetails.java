package com.example.assets_management.asset_vehicle.entity;

import com.example.assets_management.asset_insurance.entity.AssetInsurance;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_vehicle.enums.FuelType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vehicle_details", indexes = {
        @Index(name = "idx_vehicle_asset", columnList = "asset_id"),
        @Index(name = "idx_vehicle_registration", columnList = "registration_number")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_vehicle_asset", columnNames = "asset_id"),
        @UniqueConstraint(name = "uk_vehicle_registration", columnNames = "registration_number")
})
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VehicleDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "registration_number", nullable = false, length = 30)
    private String registrationNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "insurance_policy_id")
    private AssetInsurance insurancePolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, length = 20)
    private FuelType fuelType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
