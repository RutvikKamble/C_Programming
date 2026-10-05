package com.example.assets_management.asset_return.repository;
import com.example.assets_management.asset_return.entity.AssetReturn;
import com.example.assets_management.asset_return.enums.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface AssetReturnRepository extends JpaRepository<AssetReturn, UUID> {
    boolean existsByAllocationId(UUID allocationId);
    List<AssetReturn> findByAllocationId(UUID allocationId);
    java.util.Optional<AssetReturn> findFirstByAllocationIdAndStatusIn(UUID allocationId, List<ReturnStatus> statuses);
}
