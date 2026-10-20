package com.fixtrace.controller;

import com.fixtrace.dto.ApiResponse;
import com.fixtrace.dto.equipment.EquipmentResponse;
import com.fixtrace.dto.lab.CreateLabRequest;
import com.fixtrace.dto.lab.LabResponse;
import com.fixtrace.dto.lab.UpdateLabRequest;
import com.fixtrace.service.LabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labs")
@Tag(name = "Lab Management", description = "Endpoints for managing university and college laboratories")
@SecurityRequirement(name = "Bearer Authentication")
public class LabController {

    private final LabService labService;

    public LabController(LabService labService) {
        this.labService = labService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new lab", description = "Admin only. Registers a new laboratory facility.")
    public ResponseEntity<ApiResponse<LabResponse>> createLab(@Valid @RequestBody CreateLabRequest request) {
        LabResponse response = labService.createLab(request);
        return new ResponseEntity<>(ApiResponse.success("Lab created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all labs", description = "Retrieves all labs with their equipment count.")
    public ResponseEntity<ApiResponse<List<LabResponse>>> getAllLabs() {
        List<LabResponse> response = labService.getAllLabs();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lab by ID", description = "Retrieves details of a specific lab by its ID.")
    public ResponseEntity<ApiResponse<LabResponse>> getLabById(@PathVariable Long id) {
        LabResponse response = labService.getLabById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update lab", description = "Admin only. Updates details of an existing lab.")
    public ResponseEntity<ApiResponse<LabResponse>> updateLab(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLabRequest request
    ) {
        LabResponse response = labService.updateLab(id, request);
        return ResponseEntity.ok(ApiResponse.success("Lab updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete (deactivate) lab", description = "Admin only. Soft-deletes a lab by marking it inactive.")
    public ResponseEntity<ApiResponse<Void>> deleteLab(@PathVariable Long id) {
        labService.deleteLab(id);
        return ResponseEntity.ok(ApiResponse.success("Lab deactivated successfully", null));
    }

    @GetMapping("/{id}/equipment")
    @Operation(summary = "Get all equipment in a lab", description = "Retrieves all registered equipment located in the specified lab.")
    public ResponseEntity<ApiResponse<List<EquipmentResponse>>> getLabEquipment(@PathVariable Long id) {
        List<EquipmentResponse> response = labService.getLabEquipment(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
