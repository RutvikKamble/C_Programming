package com.example.assets_management.amc.entity;

import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.vendor.entity.Vendor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Annual Maintenance Contract (AMC).
 *
 * An AMC may cover either one asset or an entire asset category.  The
 * technical guide models asset_id as nullable for category-wide coverage;
 * category_id is therefore stored explicitly so that the covered category
 * can be identified without overloading the asset relationship.
 */
@Entity
@Table(name = "amc_contracts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class AmcContract {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    /** Nullable when the contract covers a whole category. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    /** Nullable when the contract covers one specific asset. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private AssetCategory category;

    @NotNull(message = "vendor is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @NotNull(message = "contractValue is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "contractValue must be zero or greater")
    @Column(name = "contract_value", precision = 12, scale = 2, nullable = false)
    private BigDecimal contractValue;

    @NotNull(message = "startDate is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "renewalDate is required")
    @Column(name = "renewal_date", nullable = false)
    private LocalDate renewalDate;
}
