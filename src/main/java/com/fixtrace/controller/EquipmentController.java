package com.fixtrace.controller;

import com.fixtrace.dto.ApiResponse;
import com.fixtrace.dto.PageResponse;
import com.fixtrace.dto.equipment.CreateEquipmentRequest;
import com.fixtrace.dto.equipment.EquipmentResponse;
import com.fixtrace.dto.equipment.MaintenanceHistoryResponse;
import com.fixtrace.dto.equipment.UpdateEquipmentRequest;
import com.fixtrace.dto.issue.IssueResponse;
import com.fixtrace.dto.repair.RepairRecordResponse;
import com.fixtrace.enums.EquipmentCategory;
import com.fixtrace.enums.EquipmentStatus;
import com.fixtrace.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@Tag(name = "Equipment Management", description = "Endpoints for managing lab assets, hardware, and equipment lifecycle")
@SecurityRequirement(name = "Bearer Authentication")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register new equipment", description = "Admin only. Registers a new laboratory equipment asset.")
    public ResponseEntity<ApiResponse<EquipmentResponse>> createEquipment(@Valid @RequestBody CreateEquipmentRequest request) {
        EquipmentResponse response = equipmentService.createEquipment(request);
        return new ResponseEntity<>(ApiResponse.success("Equipment registered successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List equipment with filtering and pagination",
            description = "Retrieves equipment matching search keywords, category, status, and lab with pagination.")
    public ResponseEntity<ApiResponse<PageResponse<EquipmentResponse>>> getAllEquipment(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EquipmentStatus status,
            @RequestParam(required = false) EquipmentCategory category,
            @RequestParam(required = false) Long labId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort
    ) {
        // Enforce maximum page size to prevent abusive requests
        int safeSize = Math.min(Math.max(size, 1), 100);

        Sort.Direction direction = Sort.Direction.DESC;
        String sortField = "id";
        if (sort.length > 0) {
            String[] parts = sort[0].split(",");
            sortField = parts[0];
            if (parts.length > 1 && parts[1].equalsIgnoreCase("asc")) {
                direction = Sort.Direction.ASC;
            }
        }

        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(direction, sortField));
        PageResponse<EquipmentResponse> response = equipmentService.getAllEquipment(search, status, category, labId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get equipment by ID", description = "Retrieves details of a specific piece of equipment.")
    public ResponseEntity<ApiResponse<EquipmentResponse>> getEquipmentById(@PathVariable Long id) {
        EquipmentResponse response = equipmentService.getEquipmentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update equipment details", description = "Admin only. Modifies properties of existing equipment.")
    public ResponseEntity<ApiResponse<EquipmentResponse>> updateEquipment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEquipmentRequest request
    ) {
        EquipmentResponse response = equipmentService.updateEquipment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Equipment updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Retire equipment", description = "Admin only. Sets status to RETIRED without deleting history.")
    public ResponseEntity<ApiResponse<Void>> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.ok(ApiResponse.success("Equipment retired successfully", null));
    }

    @GetMapping("/{id}/issues")
    @Operation(summary = "Get all issues for equipment", description = "Retrieves issue history for specific equipment.")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getEquipmentIssues(@PathVariable Long id) {
        List<IssueResponse> response = equipmentService.getEquipmentIssues(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/repair-history")
    @Operation(summary = "Get repair records for equipment", description = "Retrieves repair history for specific equipment.")
    public ResponseEntity<ApiResponse<List<RepairRecordResponse>>> getEquipmentRepairHistory(@PathVariable Long id) {
        List<RepairRecordResponse> response = equipmentService.getEquipmentRepairHistory(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/maintenance-history")
    @Operation(summary = "Get comprehensive maintenance history",
            description = "Returns aggregated downtime, failure count, repair logs, and full issue history for equipment.")
    public ResponseEntity<ApiResponse<MaintenanceHistoryResponse>> getMaintenanceHistory(@PathVariable Long id) {
        MaintenanceHistoryResponse response = equipmentService.getMaintenanceHistory(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
