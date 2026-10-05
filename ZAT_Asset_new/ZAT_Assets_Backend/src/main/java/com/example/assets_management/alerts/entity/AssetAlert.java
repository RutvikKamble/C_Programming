package com.example.assets_management.alerts.entity;
import com.example.assets_management.asset_master.entity.Asset; import jakarta.persistence.*; import lombok.*; import java.time.LocalDate;
@Entity @Table(name="asset_alert") @Getter @Setter @NoArgsConstructor public class AssetAlert {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="asset_id") private Asset asset; @Column(nullable=false) private String type; @Column(nullable=false) private String message; private LocalDate dueDate; private boolean acknowledged=false; @Column(unique=true,length=200) private String referenceKey;
}
