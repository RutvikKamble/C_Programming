package com.example.assets_management.asset_software.service;

import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.enums.AssetType;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.asset_software.dto.request.SoftwareLicenseCreateRequest;
import com.example.assets_management.asset_software.dto.request.SoftwareLicenseUpdateRequest;
import com.example.assets_management.asset_software.dto.response.SoftwareLicenseResponse;
import com.example.assets_management.asset_software.entity.SoftwareLicense;
import com.example.assets_management.asset_software.enums.SoftwareLicenseStatus;
import com.example.assets_management.asset_software.repository.SoftwareLicenseRepository;
import com.example.assets_management.common.security.EncryptionService;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.vendor.entity.Vendor;
import com.example.assets_management.vendor.repository.VendorRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class SoftwareLicenseServiceImpl implements SoftwareLicenseService {
    private final SoftwareLicenseRepository licenseRepository;
    private final AssetRepository assetRepository;
    private final VendorRepository vendorRepository;
    private final EncryptionService encryptionService;
    private final AssetHistoryService historyService;

    public SoftwareLicenseServiceImpl(SoftwareLicenseRepository licenseRepository, AssetRepository assetRepository,
                                      VendorRepository vendorRepository, EncryptionService encryptionService,
                                      AssetHistoryService historyService) {
        this.licenseRepository = licenseRepository;
        this.assetRepository = assetRepository;
        this.vendorRepository = vendorRepository;
        this.encryptionService = encryptionService;
        this.historyService = historyService;
    }

    @Override @Transactional
    public SoftwareLicenseResponse create(SoftwareLicenseCreateRequest r) {
        Asset asset = getActiveAsset(r.assetId());
        validateSoftwareAsset(asset);
        if (licenseRepository.existsByAsset_Id(asset.getId())) throw new IllegalStateException("Software license already exists for asset: " + asset.getId());
        validateDates(r.purchaseDate(), r.expiryDate(), r.terminationDate());
        SoftwareLicense l = new SoftwareLicense();
        applyCreate(l, r, asset);
        SoftwareLicense saved = licenseRepository.save(l);
        historyService.create(new AssetHistoryRequest(asset.getId(), "SOFTWARE_LICENSE_CREATED", null, null,
                "seats=" + saved.getSeatCount(), "Software license created: " + saved.getName(), r.changedBy()));
        return toResponse(saved);
    }

    private void applyCreate(SoftwareLicense l, SoftwareLicenseCreateRequest r, Asset asset) {
        l.setAsset(asset); l.setName(r.name().trim()); l.setPublisher(r.publisher().trim());
        l.setManufacturer(trim(r.manufacturer())); l.setLicenseCategory(trim(r.licenseCategory()));
        l.setSupplier(resolveVendor(r.supplierId(), asset)); l.setEncryptedLicenseKey(encryptionService.encrypt(r.licenseKey()));
        l.setSeatCount(r.seatCount()); l.setSeatsUsed(0); l.setExpiryDate(r.expiryDate()); l.setPurchaseDate(r.purchaseDate());
        l.setPurchaseCost(r.purchaseCost()); l.setRenewalCost(r.renewalCost()); l.setOrderNumber(trim(r.orderNumber()));
        l.setPurchaseOrder(trim(r.purchaseOrder())); l.setLicenseContactName(trim(r.licenseContactName()));
        l.setLicenseContactEmail(trim(r.licenseContactEmail())); l.setNotes(r.notes());
        l.setReassignable(r.reassignable() == null || r.reassignable()); l.setMaintained(r.maintained() == null || r.maintained());
        l.setTerminationDate(r.terminationDate()); l.setStatus(resolveStatus(r.expiryDate(), SoftwareLicenseStatus.ACTIVE));
    }

    @Override @Transactional(Transactional.TxType.SUPPORTS)
    public List<SoftwareLicenseResponse> findAll() { return licenseRepository.findAllByOrderByExpiryDateAsc().stream().map(this::toResponse).toList(); }

    @Override @Transactional(Transactional.TxType.SUPPORTS)
    public Page<SoftwareLicenseResponse> search(String search, String publisher, String category, SoftwareLicenseStatus status,
                                                Boolean maintained, Boolean reassignable, Pageable pageable) {
        return licenseRepository.search(blankToNull(search), blankToNull(publisher), blankToNull(category), status, maintained, reassignable, pageable).map(this::toResponse);
    }

    @Override @Transactional(Transactional.TxType.SUPPORTS)
    public SoftwareLicenseResponse findById(UUID id) { return toResponse(getLicense(id)); }

    @Override @Transactional(Transactional.TxType.SUPPORTS)
    public SoftwareLicenseResponse findByAssetId(UUID assetId) { return toResponse(licenseRepository.findByAsset_Id(assetId).orElseThrow(() -> new ResourceNotFoundException("Software license not found for asset: " + assetId))); }

    @Override @Transactional
    public SoftwareLicenseResponse update(UUID id, SoftwareLicenseUpdateRequest r) {
        SoftwareLicense l = getLicense(id); validateSoftwareAsset(l.getAsset());
        if (l.getStatus() == SoftwareLicenseStatus.CANCELLED) throw new IllegalStateException("Cancelled software license cannot be modified");
        LocalDate expiry = r.expiryDate() != null ? r.expiryDate() : l.getExpiryDate();
        LocalDate purchase = r.purchaseDate() != null ? r.purchaseDate() : l.getPurchaseDate();
        LocalDate termination = r.terminationDate() != null ? r.terminationDate() : l.getTerminationDate();
        validateDates(purchase, expiry, termination);
        StringBuilder changes = new StringBuilder();
        if (r.name()!=null) {l.setName(r.name().trim()); changes.append("name; ");}
        if (r.publisher()!=null) {l.setPublisher(r.publisher().trim()); changes.append("publisher; ");}
        if (r.manufacturer()!=null) {l.setManufacturer(trim(r.manufacturer())); changes.append("manufacturer; ");}
        if (r.licenseCategory()!=null) {l.setLicenseCategory(trim(r.licenseCategory())); changes.append("category; ");}
        if (r.supplierId()!=null) {l.setSupplier(resolveVendor(r.supplierId(), l.getAsset())); changes.append("supplier; ");}
        if (r.licenseKey()!=null && !r.licenseKey().isBlank()) {l.setEncryptedLicenseKey(encryptionService.encrypt(r.licenseKey())); changes.append("licenseKey; ");}
        if (r.seatCount()!=null) {if(r.seatCount()<l.getSeatsUsed()) throw new IllegalStateException("seatCount cannot be less than seatsUsed ("+l.getSeatsUsed()+")"); l.setSeatCount(r.seatCount()); changes.append("seats; ");}
        if (r.expiryDate()!=null) {l.setExpiryDate(r.expiryDate()); changes.append("expiryDate; ");}
        if (r.purchaseDate()!=null) {l.setPurchaseDate(r.purchaseDate()); changes.append("purchaseDate; ");}
        if (r.purchaseCost()!=null) {l.setPurchaseCost(r.purchaseCost()); changes.append("purchaseCost; ");}
        if (r.renewalCost()!=null) {l.setRenewalCost(r.renewalCost()); changes.append("renewalCost; ");}
        if (r.orderNumber()!=null) {l.setOrderNumber(trim(r.orderNumber())); changes.append("orderNumber; ");}
        if (r.purchaseOrder()!=null) {l.setPurchaseOrder(trim(r.purchaseOrder())); changes.append("purchaseOrder; ");}
        if (r.licenseContactName()!=null) {l.setLicenseContactName(trim(r.licenseContactName())); changes.append("contactName; ");}
        if (r.licenseContactEmail()!=null) {l.setLicenseContactEmail(trim(r.licenseContactEmail())); changes.append("contactEmail; ");}
        if (r.notes()!=null) {l.setNotes(r.notes()); changes.append("notes; ");}
        if (r.reassignable()!=null) {l.setReassignable(r.reassignable()); changes.append("reassignable; ");}
        if (r.maintained()!=null) {l.setMaintained(r.maintained()); changes.append("maintained; ");}
        if (r.terminationDate()!=null) {l.setTerminationDate(r.terminationDate()); changes.append("terminationDate; ");}
        if (r.status()!=null) {l.setStatus(r.status()); changes.append("status; ");}
        if (changes.isEmpty()) return toResponse(l);
        l.setStatus(resolveStatus(l.getExpiryDate(), l.getStatus()));
        SoftwareLicense saved=licenseRepository.save(l);
        historyService.create(new AssetHistoryRequest(l.getAsset().getId(), "SOFTWARE_LICENSE_UPDATED", null, null, changes.toString().trim(), "Software license updated", r.changedBy()));
        return toResponse(saved);
    }

    @Override @Transactional(Transactional.TxType.SUPPORTS)
    public List<SoftwareLicenseResponse> findExpiringSoon(int days) {
        if(days<0) throw new IllegalArgumentException("days must be zero or greater"); LocalDate today=LocalDate.now();
        return licenseRepository.findByExpiryDateBetweenOrderByExpiryDateAsc(today,today.plusDays(days)).stream().map(this::toResponse).toList();
    }

    @Override @Transactional(Transactional.TxType.SUPPORTS)
    public byte[] exportCsv() {
        StringBuilder b=new StringBuilder();
        b.append("asset_id,name,publisher,manufacturer,category,supplier_id,license_key_masked,seat_count,seats_used,expiry_date,purchase_date,purchase_cost,renewal_cost,order_number,purchase_order,license_contact_name,license_contact_email,reassignable,maintained,termination_date,status,notes\n");
        for(SoftwareLicense l:licenseRepository.findAllByOrderByExpiryDateAsc()) {
            b.append(csv(l.getAsset().getId())).append(',').append(csv(l.getName())).append(',').append(csv(l.getPublisher())).append(',').append(csv(l.getManufacturer())).append(',').append(csv(l.getLicenseCategory())).append(',').append(csv(l.getSupplier()==null?null:l.getSupplier().getId())).append(',').append(csv(maskKey(encryptionService.decrypt(l.getEncryptedLicenseKey())))).append(',').append(l.getSeatCount()).append(',').append(l.getSeatsUsed()).append(',').append(csv(l.getExpiryDate())).append(',').append(csv(l.getPurchaseDate())).append(',').append(csv(l.getPurchaseCost())).append(',').append(csv(l.getRenewalCost())).append(',').append(csv(l.getOrderNumber())).append(',').append(csv(l.getPurchaseOrder())).append(',').append(csv(l.getLicenseContactName())).append(',').append(csv(l.getLicenseContactEmail())).append(',').append(l.getReassignable()).append(',').append(l.getMaintained()).append(',').append(csv(l.getTerminationDate())).append(',').append(csv(l.getStatus())).append(',').append(csv(l.getNotes())).append('\n');
        }
        return b.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override @Transactional
    public int importCsv(byte[] csv, String changedBy) {
        String text=new String(csv, StandardCharsets.UTF_8); List<String> lines=Arrays.asList(text.split("\\R")); if(lines.size()<=1) return 0;
        int imported=0; for(int i=1;i<lines.size();i++){String line=lines.get(i).trim(); if(line.isEmpty())continue; List<String> c=parseCsv(line); if(c.size()<14) throw new IllegalArgumentException("Invalid CSV row at line "+(i+1));
            try {
                SoftwareLicenseCreateRequest r=new SoftwareLicenseCreateRequest(UUID.fromString(c.get(0)), req(c,1), req(c,2), val(c,3), val(c,4), uuid(c,5), req(c,6), Integer.valueOf(c.get(7)), LocalDate.parse(c.get(9)), date(c,10), new BigDecimal(c.get(11)), new BigDecimal(c.get(12)), val(c,13), val(c,14), val(c,15), val(c,16), val(c,21), bool(c,17), bool(c,18), date(c,19), changedBy);
                create(r); imported++;
            } catch(Exception e){ throw new IllegalArgumentException("CSV import failed at line "+(i+1)+": "+e.getMessage(),e); }
        } return imported;
    }

    private SoftwareLicense getLicense(UUID id){return licenseRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Software license not found: "+id));}
    private Asset getActiveAsset(UUID id){return assetRepository.findActiveById(id).orElseThrow(()->new ResourceNotFoundException("Active asset not found: "+id));}
    private void validateSoftwareAsset(Asset a){if(a.getAssetType()!=AssetType.SOFTWARE_ASSET)throw new IllegalArgumentException("Asset must have assetType SOFTWARE_ASSET"); if(a.getStatus()==AssetStatus.CLOSED)throw new IllegalStateException("Closed asset cannot be used for software license management");}
    private Vendor resolveVendor(UUID id, Asset asset){if(id==null)return null; Vendor v=vendorRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Vendor not found: "+id)); if(!v.getCompany().getId().equals(asset.getCompany().getId()))throw new IllegalArgumentException("Supplier and software asset must belong to the same company"); return v;}
    private void validateDates(LocalDate purchase, LocalDate expiry, LocalDate termination){if(purchase!=null && expiry!=null && expiry.isBefore(purchase))throw new IllegalArgumentException("expiryDate cannot be before purchaseDate"); if(termination!=null && expiry!=null && termination.isBefore(expiry))throw new IllegalArgumentException("terminationDate cannot be before expiryDate");}
    private SoftwareLicenseStatus resolveStatus(LocalDate expiry, SoftwareLicenseStatus current){if(current==SoftwareLicenseStatus.CANCELLED)return current;return expiry.isBefore(LocalDate.now())?SoftwareLicenseStatus.EXPIRED:SoftwareLicenseStatus.ACTIVE;}
    private SoftwareLicenseResponse toResponse(SoftwareLicense l){if(l.getExpiryDate().isBefore(LocalDate.now())&&l.getStatus()==SoftwareLicenseStatus.ACTIVE){l.setStatus(SoftwareLicenseStatus.EXPIRED);} Vendor v=l.getSupplier(); return new SoftwareLicenseResponse(l.getId(),l.getAsset().getId(),l.getAsset().getAssetCode(),l.getAsset().getName(),l.getAsset().getCompany().getId(),l.getAsset().getCompany().getCompanyName(),l.getName(),l.getPublisher(),l.getManufacturer(),l.getLicenseCategory(),v==null?null:v.getId(),v==null?null:v.getName(),maskKey(encryptionService.decrypt(l.getEncryptedLicenseKey())),l.getSeatCount(),l.getSeatsUsed(),Math.max(l.getSeatCount()-l.getSeatsUsed(),0),l.getExpiryDate(),l.getPurchaseDate(),l.getPurchaseCost(),l.getRenewalCost(),l.getOrderNumber(),l.getPurchaseOrder(),l.getLicenseContactName(),l.getLicenseContactEmail(),l.getNotes(),l.getReassignable(),l.getMaintained(),l.getTerminationDate(),l.getStatus(),l.getCreatedAt(),l.getUpdatedAt());}
    private String maskKey(String key){if(key==null||key.length()<=4)return "****";return "****-****-****-"+key.substring(key.length()-4);}
    private static String trim(String s){return s==null?null:s.trim();} private static String blankToNull(String s){return s==null||s.isBlank()?null:s.trim();}
    private static String csv(Object v){if(v==null)return "";String s=String.valueOf(v);return s.contains(",")||s.contains("\"")||s.contains("\n")?"\""+s.replace("\"","\"\"")+"\"":s;}
    private static List<String> parseCsv(String line){List<String> out=new ArrayList<>();StringBuilder b=new StringBuilder();boolean q=false;for(int i=0;i<line.length();i++){char ch=line.charAt(i);if(ch=='\"'){if(q&&i+1<line.length()&&line.charAt(i+1)=='\"'){b.append('\"');i++;}else q=!q;}else if(ch==','&&!q){out.add(b.toString());b.setLength(0);}else b.append(ch);}out.add(b.toString());return out;}
    private static String req(List<String> c,int i){if(i>=c.size()||c.get(i).isBlank())throw new IllegalArgumentException("required CSV field missing at column "+i);return c.get(i);} private static String val(List<String> c,int i){return i<c.size()&& !c.get(i).isBlank()?c.get(i):null;} private static UUID uuid(List<String> c,int i){String s=val(c,i);return s==null?null:UUID.fromString(s);} private static LocalDate date(List<String> c,int i){String s=val(c,i);return s==null?null:LocalDate.parse(s);} private static Boolean bool(List<String> c,int i){String s=val(c,i);return s==null?null:Boolean.valueOf(s);}
}
