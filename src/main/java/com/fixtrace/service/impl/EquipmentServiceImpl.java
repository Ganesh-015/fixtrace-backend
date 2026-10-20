package com.fixtrace.service.impl;

import com.fixtrace.dto.PageResponse;
import com.fixtrace.dto.equipment.CreateEquipmentRequest;
import com.fixtrace.dto.equipment.EquipmentResponse;
import com.fixtrace.dto.equipment.MaintenanceHistoryResponse;
import com.fixtrace.dto.equipment.UpdateEquipmentRequest;
import com.fixtrace.dto.issue.IssueResponse;
import com.fixtrace.dto.repair.RepairRecordResponse;
import com.fixtrace.entity.Equipment;
import com.fixtrace.entity.Issue;
import com.fixtrace.entity.Lab;
import com.fixtrace.entity.RepairRecord;
import com.fixtrace.enums.EquipmentCategory;
import com.fixtrace.enums.EquipmentStatus;
import com.fixtrace.exception.DuplicateResourceException;
import com.fixtrace.exception.ResourceNotFoundException;
import com.fixtrace.mapper.EquipmentMapper;
import com.fixtrace.mapper.IssueMapper;
import com.fixtrace.mapper.RepairRecordMapper;
import com.fixtrace.repository.EquipmentRepository;
import com.fixtrace.repository.IssueRepository;
import com.fixtrace.repository.LabRepository;
import com.fixtrace.repository.RepairRecordRepository;
import com.fixtrace.service.EquipmentService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private static final Logger log = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final EquipmentRepository equipmentRepository;
    private final LabRepository labRepository;
    private final IssueRepository issueRepository;
    private final RepairRecordRepository repairRecordRepository;
    private final EquipmentMapper equipmentMapper;
    private final IssueMapper issueMapper;
    private final RepairRecordMapper repairRecordMapper;

    public EquipmentServiceImpl(
            EquipmentRepository equipmentRepository,
            LabRepository labRepository,
            IssueRepository issueRepository,
            RepairRecordRepository repairRecordRepository,
            EquipmentMapper equipmentMapper,
            IssueMapper issueMapper,
            RepairRecordMapper repairRecordMapper
    ) {
        this.equipmentRepository = equipmentRepository;
        this.labRepository = labRepository;
        this.issueRepository = issueRepository;
        this.repairRecordRepository = repairRecordRepository;
        this.equipmentMapper = equipmentMapper;
        this.issueMapper = issueMapper;
        this.repairRecordMapper = repairRecordMapper;
    }

    @Override
    @Transactional
    public EquipmentResponse createEquipment(CreateEquipmentRequest request) {
        log.info("Creating equipment with asset code: {}", request.getAssetCode());

        String assetCode = request.getAssetCode().trim().toUpperCase();
        if (equipmentRepository.existsByAssetCode(assetCode)) {
            throw new DuplicateResourceException("Equipment", "assetCode", assetCode);
        }

        String serialNumber = request.getSerialNumber().trim();
        if (equipmentRepository.existsBySerialNumber(serialNumber)) {
            throw new DuplicateResourceException("Equipment", "serialNumber", serialNumber);
        }

        Lab lab = labRepository.findById(request.getLabId())
                .orElseThrow(() -> new ResourceNotFoundException("Lab", "id", request.getLabId()));

        Equipment equipment = new Equipment();
        equipment.setAssetCode(assetCode);
        equipment.setName(request.getName().trim());
        equipment.setCategory(request.getCategory());
        equipment.setBrand(request.getBrand() != null ? request.getBrand().trim() : null);
        equipment.setModel(request.getModel() != null ? request.getModel().trim() : null);
        equipment.setSerialNumber(serialNumber);
        equipment.setPurchaseDate(request.getPurchaseDate());
        equipment.setWarrantyExpiryDate(request.getWarrantyExpiryDate());
        equipment.setStatus(request.getStatus() != null ? request.getStatus() : EquipmentStatus.WORKING);
        equipment.setLab(lab);
        equipment.setDescription(request.getDescription());

        Equipment saved = equipmentRepository.save(equipment);
        log.info("Equipment created successfully with ID: {}", saved.getId());

        return equipmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EquipmentResponse> getAllEquipment(
            String search,
            EquipmentStatus status,
            EquipmentCategory category,
            Long labId,
            Pageable pageable
    ) {
        Specification<Equipment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate assetCodeMatch = cb.like(cb.lower(root.get("assetCode")), searchPattern);
                Predicate brandMatch = cb.like(cb.lower(root.get("brand")), searchPattern);
                Predicate modelMatch = cb.like(cb.lower(root.get("model")), searchPattern);
                Predicate serialMatch = cb.like(cb.lower(root.get("serialNumber")), searchPattern);
                predicates.add(cb.or(nameMatch, assetCodeMatch, brandMatch, modelMatch, serialMatch));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (labId != null) {
                predicates.add(cb.equal(root.get("lab").get("id"), labId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Equipment> equipmentPage = equipmentRepository.findAll(spec, pageable);
        List<EquipmentResponse> content = equipmentPage.getContent().stream()
                .map(equipmentMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(equipmentPage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentResponse getEquipmentById(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", "id", id));
        return equipmentMapper.toResponse(equipment);
    }

    @Override
    @Transactional
    public EquipmentResponse updateEquipment(Long id, UpdateEquipmentRequest request) {
        log.info("Updating equipment with ID: {}", id);

        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", "id", id));

        if (StringUtils.hasText(request.getName())) {
            equipment.setName(request.getName().trim());
        }
        if (request.getCategory() != null) {
            equipment.setCategory(request.getCategory());
        }
        if (request.getBrand() != null) {
            equipment.setBrand(request.getBrand().trim());
        }
        if (request.getModel() != null) {
            equipment.setModel(request.getModel().trim());
        }
        if (StringUtils.hasText(request.getSerialNumber())
                && !request.getSerialNumber().trim().equalsIgnoreCase(equipment.getSerialNumber())) {
            String newSerial = request.getSerialNumber().trim();
            if (equipmentRepository.existsBySerialNumber(newSerial)) {
                throw new DuplicateResourceException("Equipment", "serialNumber", newSerial);
            }
            equipment.setSerialNumber(newSerial);
        }
        if (request.getPurchaseDate() != null) {
            equipment.setPurchaseDate(request.getPurchaseDate());
        }
        if (request.getWarrantyExpiryDate() != null) {
            equipment.setWarrantyExpiryDate(request.getWarrantyExpiryDate());
        }
        if (request.getStatus() != null) {
            equipment.setStatus(request.getStatus());
        }
        if (request.getLabId() != null) {
            Lab lab = labRepository.findById(request.getLabId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lab", "id", request.getLabId()));
            equipment.setLab(lab);
        }
        if (request.getDescription() != null) {
            equipment.setDescription(request.getDescription());
        }

        Equipment updated = equipmentRepository.save(equipment);
        log.info("Equipment updated successfully: {}", updated.getId());

        return equipmentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteEquipment(Long id) {
        log.info("Retiring equipment with ID: {}", id);

        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", "id", id));

        // Soft-delete/retire to preserve historical repair integrity
        equipment.setStatus(EquipmentStatus.RETIRED);
        equipmentRepository.save(equipment);
        log.info("Equipment marked as RETIRED: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getEquipmentIssues(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipment", "id", id);
        }

        return issueRepository.findByEquipmentIdOrderByReportedAtDesc(id).stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairRecordResponse> getEquipmentRepairHistory(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipment", "id", id);
        }

        return repairRecordRepository.findByIssueEquipmentIdOrderByCreatedAtDesc(id).stream()
                .map(repairRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceHistoryResponse getMaintenanceHistory(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", "id", id));

        List<Issue> issues = issueRepository.findByEquipmentIdOrderByReportedAtDesc(id);
        List<RepairRecord> allRepairRecords = repairRecordRepository.findByIssueEquipmentIdOrderByCreatedAtDesc(id);

        MaintenanceHistoryResponse response = new MaintenanceHistoryResponse();
        response.setEquipmentId(equipment.getId());
        response.setAssetCode(equipment.getAssetCode());
        response.setName(equipment.getName());
        response.setCategory(equipment.getCategory());
        response.setStatus(equipment.getStatus());
        if (equipment.getLab() != null) {
            response.setLabCode(equipment.getLab().getLabCode());
            response.setLabName(equipment.getLab().getName());
        }

        response.setTotalFailures(issues.size());
        response.setTotalRepairs(allRepairRecords.size());

        double totalDowntimeHours = 0.0;
        List<MaintenanceHistoryResponse.HistoricalIssueItem> issueItems = new ArrayList<>();

        for (Issue issue : issues) {
            MaintenanceHistoryResponse.HistoricalIssueItem item = new MaintenanceHistoryResponse.HistoricalIssueItem();
            item.setIssueId(issue.getId());
            item.setIssueCode(issue.getIssueCode());
            item.setTitle(issue.getTitle());
            item.setDescription(issue.getDescription());
            item.setPriority(issue.getPriority());
            item.setStatus(issue.getStatus());
            if (issue.getReportedBy() != null) {
                item.setReportedByName(issue.getReportedBy().getName());
            }
            item.setReportedAt(issue.getReportedAt());

            if (issue.getAssignedTechnician() != null) {
                item.setTechnicianName(issue.getAssignedTechnician().getName());
            }
            item.setAssignedAt(issue.getAssignedAt());
            item.setResolvedAt(issue.getResolvedAt());

            if (issue.getReportedAt() != null && issue.getResolvedAt() != null) {
                long minutes = Duration.between(issue.getReportedAt(), issue.getResolvedAt()).toMinutes();
                double hours = Math.round((minutes / 60.0) * 100.0) / 100.0;
                item.setDowntimeHours(hours);
                totalDowntimeHours += hours;
            }

            // Repairs for this issue
            List<RepairRecord> issueRepairs = repairRecordRepository.findByIssueIdOrderByCreatedAtDesc(issue.getId());
            List<MaintenanceHistoryResponse.HistoricalRepairItem> repairItems = issueRepairs.stream().map(r -> {
                MaintenanceHistoryResponse.HistoricalRepairItem repairItem = new MaintenanceHistoryResponse.HistoricalRepairItem();
                repairItem.setRepairId(r.getId());
                if (r.getTechnician() != null) {
                    repairItem.setTechnicianName(r.getTechnician().getName());
                }
                repairItem.setDiagnosis(r.getDiagnosis());
                repairItem.setActionTaken(r.getActionTaken());
                repairItem.setPartsUsed(r.getPartsUsed());
                repairItem.setRepairNotes(r.getRepairNotes());
                repairItem.setRepairStartedAt(r.getRepairStartedAt());
                repairItem.setRepairCompletedAt(r.getRepairCompletedAt());
                return repairItem;
            }).collect(Collectors.toList());

            item.setRepairRecords(repairItems);
            issueItems.add(item);
        }

        response.setTotalDowntimeHours(Math.round(totalDowntimeHours * 100.0) / 100.0);
        response.setIssues(issueItems);

        return response;
    }
}
