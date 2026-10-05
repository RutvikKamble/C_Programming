package com.example.assets_management.role_permission.entity;

import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.role.entity.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name="role_permissions", uniqueConstraints=@UniqueConstraint(name="uk_role_permissions_role_permission",columnNames={"role_id","permission_id"}))
@Getter @Setter @NoArgsConstructor
public class RolePermission {
 @Id @GeneratedValue(strategy=GenerationType.UUID) @Column(nullable=false,updatable=false) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="role_id",nullable=false,foreignKey=@ForeignKey(name="fk_role_permissions_role")) private Role role;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="permission_id",nullable=false,foreignKey=@ForeignKey(name="fk_role_permissions_permission")) private Permission permission;
 @CreationTimestamp @Column(name="assigned_at",nullable=false,updatable=false) private LocalDateTime assignedAt;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="assigned_by",foreignKey=@ForeignKey(name="fk_role_permissions_assigned_by")) private Employee assignedBy;
}
