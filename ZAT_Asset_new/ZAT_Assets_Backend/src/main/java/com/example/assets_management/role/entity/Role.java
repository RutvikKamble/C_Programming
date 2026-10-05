package com.example.assets_management.role.entity;

import com.example.assets_management.company.entity.Company;
import com.example.assets_management.role.enums.RoleStatus;
import com.example.assets_management.role.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="roles", uniqueConstraints=@UniqueConstraint(name="uk_roles_company_code", columnNames={"company_id","code"}))
@Getter @Setter @NoArgsConstructor
public class Role {
    @Id @GeneratedValue(strategy=GenerationType.UUID) @Column(nullable=false, updatable=false)
    private UUID id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="company_id", foreignKey=@ForeignKey(name="fk_roles_company"))
    private Company company;
    @Column(name="name", nullable=false, length=100) private String name;
    @Column(name="code", length=80) private String code;
    @Column(name="description", length=500) private String description;
    @Enumerated(EnumType.STRING) @Column(name="type", nullable=false, length=20) private RoleType type=RoleType.CUSTOM;
    @Enumerated(EnumType.STRING) @Column(name="status", nullable=false, length=20) private RoleStatus status=RoleStatus.ACTIVE;
    @Column(name="system_defined", nullable=false) private boolean systemDefined=false;
    @Column(name="is_system", nullable=false) private boolean systemRole=false;
    @Column(name="is_active", nullable=false) private boolean active=true;
    /**
     * Set once the built-in default permission set has been seeded for this role, so a restart never
     * re-adds permissions an administrator deliberately removed from a built-in role.
     */
    @Column(name="default_permissions_seeded", nullable=false, columnDefinition="boolean default false")
    private boolean defaultPermissionsSeeded=false;
    @CreationTimestamp @Column(name="created_at", nullable=false, updatable=false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name="updated_at") private LocalDateTime updatedAt;
    @PrePersist @PreUpdate private void sync() {
        if(type==null) type=RoleType.CUSTOM; if(status==null) status=RoleStatus.ACTIVE;
        systemDefined=(type==RoleType.SYSTEM); systemRole=systemDefined; active=(status==RoleStatus.ACTIVE);
        if(code==null || code.isBlank()) code=name==null?null:name.trim().toUpperCase().replaceAll("[^A-Z0-9]+","_");
    }
}
