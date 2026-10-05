package com.example.assets_management.user_permission.entity;

import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.user_permission.enums.UserPermissionSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A permission held by one specific user UUID.
 *
 * <p>This table is the single source of truth for what a user may do. When a role is assigned to a
 * user, every permission of that role is copied here as a {@link UserPermissionSource#ROLE} row keyed
 * on the user's UUID. From then on:
 * <ul>
 *   <li>editing the role's permissions re-syncs every user holding that role;</li>
 *   <li>an administrator can add a permission to one user only ({@link UserPermissionSource#DIRECT});</li>
 *   <li>an administrator can remove an inherited permission from one user only - the ROLE row is kept
 *       and flagged {@code revoked}, so later role syncs do not silently give it back.</li>
 * </ul>
 * A permission is effective for the user only while {@code revoked = false}.
 */
@Entity
@Table(
        name = "user_permissions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_permissions_user_permission",
                        columnNames = {"user_id", "permission_id"}
                )
        },
        indexes = {
                @Index(name = "idx_user_permissions_user", columnList = "user_id"),
                @Index(name = "idx_user_permissions_permission", columnList = "permission_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_permissions_user")
    )
    private Employee user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "permission_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_permissions_permission")
    )
    private Permission permission;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private UserPermissionSource source = UserPermissionSource.ROLE;

    /** True when an inherited (ROLE) permission was removed from this user only. */
    @Column(name = "revoked", nullable = false, columnDefinition = "boolean default false")
    private boolean revoked = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Convenience: the permission is currently usable by the user. */
    public boolean isEffective() {
        return !revoked;
    }
}
