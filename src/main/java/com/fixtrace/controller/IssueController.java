package com.fixtrace.controller;

import com.fixtrace.dto.ApiResponse;
import com.fixtrace.dto.PageResponse;
import com.fixtrace.dto.issue.*;
import com.fixtrace.dto.repair.RepairRecordRequest;
import com.fixtrace.dto.repair.RepairRecordResponse;
import com.fixtrace.enums.IssuePriority;
import com.fixtrace.enums.IssueStatus;
import com.fixtrace.service.IssueService;
import com.fixtrace.service.RepairRecordService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@Tag(name = "Issue Management", description = "Endpoints for reporting lab maintenance issues, technician assignment, and lifecycle transitions")
@SecurityRequirement(name = "Bearer Authentication")
public class IssueController {

    private final IssueService issueService;
    private final RepairRecordService repairRecordService;

    public IssueController(IssueService issueService, RepairRecordService repairRecordService) {
        this.issueService = issueService;
        this.repairRecordService = repairRecordService;
    }

    @PostMapping
    @Operation(summary = "Report a new issue", description = "Students, Staff, Technicians, or Admins can report equipment faults. Reporter is obtained automatically.")
    public ResponseEntity<ApiResponse<IssueResponse>> reportIssue(
            @Valid @RequestBody CreateIssueRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        IssueResponse response = issueService.reportIssue(request, userDetails.getUsername());
        return new ResponseEntity<>(ApiResponse.success("Issue reported successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List issues with filters and pagination",
            description = "Retrieves issues filtered by status, priority, lab, technician, or search terms. Students/Staff see their own issues.")
    public ResponseEntity<ApiResponse<PageResponse<IssueResponse>>> getIssues(
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) IssuePriority priority,
            @RequestParam(required = false) Long labId,
            @RequestParam(required = false) Long technicianId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
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
        PageResponse<IssueResponse> response = issueService.getIssues(
                status, priority, labId, technicianId, search, pageable, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get issue details by ID", description = "Retrieves details of an issue by its ID.")
    public ResponseEntity<ApiResponse<IssueResponse>> getIssueById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        IssueResponse response = issueService.getIssueById(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign technician to issue", description = "Admin only. Assigns a designated technician to resolve the issue.")
    public ResponseEntity<ApiResponse<IssueResponse>> assignIssue(
            @PathVariable Long id,
            @Valid @RequestBody AssignIssueRequest request
    ) {
        IssueResponse response = issueService.assignIssue(id, request);
        return ResponseEntity.ok(ApiResponse.success("Technician assigned successfully", response));
    }

    @PutMapping("/{id}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update issue priority", description = "Admin only. Updates priority (LOW, MEDIUM, HIGH, CRITICAL).")
    public ResponseEntity<ApiResponse<IssueResponse>> updatePriority(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIssuePriorityRequest request
    ) {
        IssueResponse response = issueService.updatePriority(id, request);
        return ResponseEntity.ok(ApiResponse.success("Issue priority updated successfully", response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    @Operation(summary = "Update issue lifecycle status", description = "Technician or Admin. Transitions status through allowed workflow states.")
    public ResponseEntity<ApiResponse<IssueResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIssueStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        IssueResponse response = issueService.updateStatus(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Issue status updated successfully", response));
    }

    @PutMapping("/{id}/close")
    @Operation(summary = "Close a resolved issue", description = "Reporter or Admin. Closes an issue that has already reached RESOLVED status.")
    public ResponseEntity<ApiResponse<IssueResponse>> closeIssue(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        IssueResponse response = issueService.closeIssue(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Issue closed successfully", response));
    }

    @PutMapping("/{id}/reopen")
    @Operation(summary = "Reopen a resolved or closed issue", description = "Reporter or Admin. Reopens an issue for additional work.")
    public ResponseEntity<ApiResponse<IssueResponse>> reopenIssue(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        IssueResponse response = issueService.reopenIssue(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Issue reopened successfully", response));
    }

    // Nested repair record endpoints as requested in Section 12
    @PostMapping("/{issueId}/repair-records")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    @Operation(summary = "Create repair record for issue", description = "Technician or Admin records diagnosis, parts used, and repair actions taken.")
    public ResponseEntity<ApiResponse<RepairRecordResponse>> createRepairRecord(
            @PathVariable Long issueId,
            @Valid @RequestBody RepairRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        RepairRecordResponse response = repairRecordService.createRepairRecord(issueId, request, userDetails.getUsername());
        return new ResponseEntity<>(ApiResponse.success("Repair record logged successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/{issueId}/repair-records")
    @Operation(summary = "Get all repair records for issue", description = "Retrieves all logged repair activities associated with an issue.")
    public ResponseEntity<ApiResponse<List<RepairRecordResponse>>> getRepairRecordsForIssue(@PathVariable Long issueId) {
        List<RepairRecordResponse> response = repairRecordService.getRepairRecordsForIssue(issueId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
