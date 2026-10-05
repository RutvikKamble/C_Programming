package com.example.assets_management.asset_category.entity;

import com.example.assets_management.asset_category.enums.AssetCategoryType;
import com.example.assets_management.company.entity.Company;
import com.example.assets_management.asset_category.enums.DepreciationMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "asset_categories")
@Getter
@Setter
@NoArgsConstructor
public class AssetCategory {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private AssetCategory parentCategory;

    @Column(name = "category", nullable = false, length = 100)
    private String assetCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "depreciation_method", nullable = false)
    private DepreciationMethod depreciationMethod;

    @Column(name = "useful_life_months")
    private Integer usefulLifeMonths;
}
