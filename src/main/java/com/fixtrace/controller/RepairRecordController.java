package com.fixtrace.controller;

import com.fixtrace.dto.ApiResponse;
import com.fixtrace.dto.repair.RepairRecordRequest;
import com.fixtrace.dto.repair.RepairRecordResponse;
import com.fixtrace.service.RepairRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repair-records")
@Tag(name = "Repair Records", description = "Endpoints for managing technical repair records and action logs")
@SecurityRequirement(name = "Bearer Authentication")
public class RepairRecordController {

    private final RepairRecordService repairRecordService;

    public RepairRecordController(RepairRecordService repairRecordService) {
        this.repairRecordService = repairRecordService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get repair record by ID", description = "Retrieves details of a specific technical repair entry.")
    public ResponseEntity<ApiResponse<RepairRecordResponse>> getRepairRecordById(@PathVariable Long id) {
        RepairRecordResponse response = repairRecordService.getRepairRecordById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    @Operation(summary = "Update repair record", description = "Technician who authored the record or Admin can update repair details.")
    public ResponseEntity<ApiResponse<RepairRecordResponse>> updateRepairRecord(
            @PathVariable Long id,
            @Valid @RequestBody RepairRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        RepairRecordResponse response = repairRecordService.updateRepairRecord(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Repair record updated successfully", response));
    }
}
