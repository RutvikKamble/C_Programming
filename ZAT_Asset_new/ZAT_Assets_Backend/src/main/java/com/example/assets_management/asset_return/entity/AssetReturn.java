package com.example.assets_management.asset_return.entity;

import com.example.assets_management.asset_allocation.entity.Allocation;
import com.example.assets_management.asset_return.enums.ReturnReason;
import com.example.assets_management.asset_return.enums.ReturnStatus;
import com.example.assets_management.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "asset_returns")
@Getter
@Setter
@NoArgsConstructor
public class AssetReturn {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    // asset_id / allocation_id (you can choose one, here using allocation)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allocation_id", nullable = false)
    private Allocation allocation;

    // requested_by ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ users
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requested_by", nullable = false)
    private Employee requestedBy;

    // reason ENUM
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private ReturnReason reason;

    // status ENUM
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReturnStatus status;

    // verified_by ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ users
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "verified_by")
    private Employee verifiedBy;

    // verified_on ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ DATE
    @Column(name = "verified_on")
    private LocalDate verifiedOn;
}
