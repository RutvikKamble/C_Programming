package com.example.assets_management.alerts.repository;
import com.example.assets_management.alerts.entity.AssetAlert; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface AssetAlertRepository extends JpaRepository<AssetAlert,Long>{List<AssetAlert> findByAcknowledgedFalseOrderByDueDateAsc(); boolean existsByReferenceKey(String referenceKey);}
