package com.fixtrace.service;

import com.fixtrace.dto.PageResponse;
import com.fixtrace.dto.equipment.CreateEquipmentRequest;
import com.fixtrace.dto.equipment.EquipmentResponse;
import com.fixtrace.dto.equipment.MaintenanceHistoryResponse;
import com.fixtrace.dto.equipment.UpdateEquipmentRequest;
import com.fixtrace.dto.issue.IssueResponse;
import com.fixtrace.dto.repair.RepairRecordResponse;
import com.fixtrace.enums.EquipmentCategory;
import com.fixtrace.enums.EquipmentStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EquipmentService {

    EquipmentResponse createEquipment(CreateEquipmentRequest request);

    PageResponse<EquipmentResponse> getAllEquipment(
            String search,
            EquipmentStatus status,
            EquipmentCategory category,
            Long labId,
            Pageable pageable
    );

    EquipmentResponse getEquipmentById(Long id);

    EquipmentResponse updateEquipment(Long id, UpdateEquipmentRequest request);

    void deleteEquipment(Long id);

    List<IssueResponse> getEquipmentIssues(Long id);

    List<RepairRecordResponse> getEquipmentRepairHistory(Long id);

    MaintenanceHistoryResponse getMaintenanceHistory(Long id);
}
