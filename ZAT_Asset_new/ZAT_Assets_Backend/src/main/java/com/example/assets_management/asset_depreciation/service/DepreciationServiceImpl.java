package com.example.assets_management.asset_depreciation.service;

import com.example.assets_management.asset_category.enums.DepreciationMethod;
import com.example.assets_management.asset_depreciation.dto.request.DepreciationPreviewRequest;
import com.example.assets_management.asset_depreciation.dto.response.*;
import com.example.assets_management.asset_depreciation.engine.DecliningBalanceCalculator;
import com.example.assets_management.asset_depreciation.engine.StraightLineCalculator;
import com.example.assets_management.asset_depreciation.entity.DepreciationRecord;
import com.example.assets_management.asset_depreciation.enums.DepreciationStatus;
import com.example.assets_management.asset_depreciation.repository.DepreciationRecordRepository;
import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.repository.AssetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepreciationServiceImpl implements DepreciationService {
    private final AssetRepository assetRepository;
    private final DepreciationRecordRepository recordRepository;
    private final AssetHistoryService historyService;
    private final StraightLineCalculator straightLineCalculator;
    private final DecliningBalanceCalculator decliningBalanceCalculator;

    @Override
    public List<DepreciationResponse> getHistory(UUID assetId) {
        assetRepository.findActiveById(assetId).orElseThrow(() -> new RuntimeException("Asset not found"));
        return recordRepository.findByAssetIdOrderByPeriodMonthAsc(assetId).stream().map(this::toResponse).toList();
    }

    @Override
    public DepreciationSummaryResponse getSummary(UUID assetId) {
        Asset asset = assetRepository.findActiveById(assetId).orElseThrow(() -> new RuntimeException("Asset not found"));
        List<DepreciationRecord> records = recordRepository.findByAssetIdOrderByPeriodMonthAsc(assetId);
        BigDecimal accumulated = records.isEmpty() ? BigDecimal.ZERO : records.get(records.size() - 1).getAccumulatedDepreciation();
        BigDecimal current = asset.getCurrentValue() != null ? asset.getCurrentValue() : nz(asset.getPurchaseCost());
        Integer life = asset.getUsefulLifeMonths();
        LocalDate start = asset.getDepreciationStartDate();
        long elapsed = start == null ? 0 : Math.max(0, ChronoUnit.MONTHS.between(start.withDayOfMonth(1), LocalDate.now().withDayOfMonth(1)) + (records.isEmpty() ? 0 : 1));
        int elapsedMonths = life == null ? 0 : (int) Math.min(elapsed, life);
        int remaining = life == null ? 0 : Math.max(0, life - elapsedMonths);
        DepreciationStatus status = asset.getDepreciationStatus();
        if (status == null) status = resolveStatus(asset, LocalDate.now().withDayOfMonth(1));
        return new DepreciationSummaryResponse(assetId, asset.getAssetCode(), nz(asset.getPurchaseCost()), nz(asset.getSalvageValue()), current,
                accumulated, asset.getDepreciationMethod(), life, elapsedMonths, remaining, status);
    }

    @Override
    public DepreciationPreviewResponse preview(DepreciationPreviewRequest request) {
        validateConfig(request.purchaseCost(), request.salvageValue(), request.usefulLifeMonths(), request.depreciationMethod(), request.openingValue());
        BigDecimal depreciation = calculate(request.depreciationMethod(), request.purchaseCost(), request.salvageValue(), request.usefulLifeMonths(), request.openingValue());
        BigDecimal closing = request.openingValue().subtract(depreciation).max(request.salvageValue()).setScale(2, RoundingMode.HALF_UP);
        return new DepreciationPreviewResponse(request.depreciationMethod(), depreciation.setScale(2, RoundingMode.HALF_UP), closing);
    }

    @Override
    @Transactional
    public int run(LocalDate periodMonth) {
        LocalDate period = periodMonth.withDayOfMonth(1);
        int processed = 0;
        for (Asset asset : assetRepository.findAllActive()) {
            try {
                if (processAsset(asset, period)) processed++;
            } catch (RuntimeException ignored) {
                // A malformed depreciation configuration for one asset must not
                // prevent other eligible assets from being processed.
            }
        }
        return processed;
    }

    @Transactional
    protected boolean processAsset(Asset asset, LocalDate period) {
        if (asset.getStatus() == AssetStatus.CLOSED || asset.getDeletedAt() != null) return false;
        DepreciationMethod method = effectiveMethod(asset);
        if (method == null || method == DepreciationMethod.NONE) {
            if (asset.getDepreciationStatus() == null || asset.getDepreciationStatus() == DepreciationStatus.NOT_STARTED) {
                asset.setDepreciationStatus(DepreciationStatus.STOPPED);
                assetRepository.save(asset);
            }
            return false;
        }
        if (method != DepreciationMethod.STRAIGHT_LINE && method != DepreciationMethod.DECLINING_BALANCE) return false;
        if (asset.getDepreciationStatus() == DepreciationStatus.SUSPENDED || asset.getDepreciationStatus() == DepreciationStatus.STOPPED) return false;
        LocalDate start = asset.getDepreciationStartDate() != null ? asset.getDepreciationStartDate().withDayOfMonth(1) :
                (asset.getPurchaseDate() != null ? asset.getPurchaseDate().withDayOfMonth(1) : period);
        if (period.isBefore(start)) return false;
        if (recordRepository.existsByAssetIdAndPeriodMonth(asset.getId(), period)) return false;

        BigDecimal purchase = nz(asset.getPurchaseCost());
        BigDecimal salvage = nz(asset.getSalvageValue());
        Integer life = asset.getUsefulLifeMonths();
        BigDecimal opening = asset.getCurrentValue() != null ? asset.getCurrentValue() : purchase;
        validateConfig(purchase, salvage, life, method, opening);
        if (opening.compareTo(salvage) <= 0) {
            asset.setCurrentValue(salvage);
            asset.setDepreciationStatus(DepreciationStatus.FULLY_DEPRECIATED);
            assetRepository.save(asset);
            return false;
        }

        BigDecimal depreciation = calculate(method, purchase, salvage, life, opening).setScale(2, RoundingMode.HALF_UP);
        BigDecimal closing = opening.subtract(depreciation).max(salvage).setScale(2, RoundingMode.HALF_UP);
        BigDecimal previousAccumulated = recordRepository.findTopByAssetIdOrderByPeriodMonthDesc(asset.getId())
                .map(DepreciationRecord::getAccumulatedDepreciation).orElse(BigDecimal.ZERO);
        BigDecimal accumulated = previousAccumulated.add(depreciation).setScale(2, RoundingMode.HALF_UP);
        DepreciationStatus status = closing.compareTo(salvage) <= 0 ? DepreciationStatus.FULLY_DEPRECIATED : DepreciationStatus.ACTIVE;

        DepreciationRecord record = new DepreciationRecord();
        record.setAsset(asset);
        record.setPeriodMonth(period);
        record.setOpeningValue(opening.setScale(2, RoundingMode.HALF_UP));
        record.setDepreciationAmount(depreciation);
        record.setAccumulatedDepreciation(accumulated);
        record.setClosingValue(closing);
        record.setDepreciationMethod(method);
        record.setDepreciationStatus(status);
        recordRepository.save(record);

        asset.setCurrentValue(closing);
        asset.setDepreciationStatus(status);
        if (asset.getDepreciationEndDate() == null) asset.setDepreciationEndDate(start.plusMonths(life).minusDays(1));
        assetRepository.save(asset);

        historyService.create(new AssetHistoryRequest(asset.getId(), "DEPRECIATION_RECORDED", "currentValue",
                opening.toPlainString(), closing.toPlainString(),
                "Depreciation recorded for " + period + ": " + depreciation, "SYSTEM"));
        return true;
    }

    private BigDecimal calculate(DepreciationMethod method, BigDecimal purchase, BigDecimal salvage, int life, BigDecimal opening) {
        return switch (method) {
            case STRAIGHT_LINE -> straightLineCalculator.calculate(purchase, salvage, life, opening);
            case DECLINING_BALANCE -> decliningBalanceCalculator.calculate(purchase, salvage, life, opening);
            case NONE -> BigDecimal.ZERO;
            default -> throw new IllegalArgumentException("Depreciation method is not supported yet: " + method);
        };
    }

    private void validateConfig(BigDecimal purchase, BigDecimal salvage, Integer life, DepreciationMethod method, BigDecimal opening) {
        if (purchase == null || purchase.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("purchaseCost must be >= 0");
        if (salvage == null || salvage.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("salvageValue must be >= 0");
        if (method == null) throw new IllegalArgumentException("depreciationMethod is required");
        if (method != DepreciationMethod.NONE && (life == null || life <= 0)) throw new IllegalArgumentException("usefulLifeMonths must be > 0");
        if (purchase.compareTo(salvage) < 0) throw new IllegalArgumentException("salvageValue cannot exceed purchaseCost");
        if (opening != null && opening.compareTo(salvage) < 0) throw new IllegalArgumentException("openingValue cannot be below salvageValue");
    }

    private DepreciationMethod effectiveMethod(Asset asset) {
        return asset.getDepreciationMethod() != null ? asset.getDepreciationMethod() : asset.getCategory().getDepreciationMethod();
    }

    private DepreciationStatus resolveStatus(Asset asset, LocalDate period) {
        DepreciationMethod method = effectiveMethod(asset);
        if (method == null || method == DepreciationMethod.NONE) return DepreciationStatus.STOPPED;
        LocalDate start = asset.getDepreciationStartDate() != null ? asset.getDepreciationStartDate() : asset.getPurchaseDate();
        if (start != null && period.isBefore(start.withDayOfMonth(1))) return DepreciationStatus.NOT_STARTED;
        if (asset.getCurrentValue() != null && asset.getSalvageValue() != null && asset.getCurrentValue().compareTo(asset.getSalvageValue()) <= 0) return DepreciationStatus.FULLY_DEPRECIATED;
        return DepreciationStatus.ACTIVE;
    }

    private DepreciationResponse toResponse(DepreciationRecord r) {
        return new DepreciationResponse(r.getId(), r.getAsset().getId(), r.getAsset().getAssetCode(), r.getAsset().getName(), r.getPeriodMonth(),
                r.getOpeningValue(), r.getDepreciationAmount(), r.getAccumulatedDepreciation(), r.getClosingValue(), r.getDepreciationMethod(), r.getDepreciationStatus(), r.getCreatedAt());
    }

    @Override
    public List<DepreciationResponse> report(UUID companyId, UUID branchId, UUID departmentId, UUID categoryId, UUID assetId, String method, LocalDate periodMonth) {
        return recordRepository.findAllForReport().stream()
                .filter(r -> companyId == null || r.getAsset().getCompany().getId().equals(companyId))
                .filter(r -> branchId == null || (r.getAsset().getBranch() != null && r.getAsset().getBranch().getId().equals(branchId)))
                .filter(r -> departmentId == null || (r.getAsset().getDepartment() != null && r.getAsset().getDepartment().getId().equals(departmentId)))
                .filter(r -> categoryId == null || r.getAsset().getCategory().getId().equals(categoryId))
                .filter(r -> assetId == null || r.getAsset().getId().equals(assetId))
                .filter(r -> method == null || r.getDepreciationMethod().name().equalsIgnoreCase(method))
                .filter(r -> periodMonth == null || r.getPeriodMonth().equals(periodMonth.withDayOfMonth(1)))
                .map(this::toResponse).toList();
    }

    private static BigDecimal nz(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
}
