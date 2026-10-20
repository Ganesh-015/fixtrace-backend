package com.fixtrace.controller;

import com.fixtrace.dto.ApiResponse;
import com.fixtrace.dto.dashboard.ChartItemResponse;
import com.fixtrace.dto.dashboard.DashboardSummaryResponse;
import com.fixtrace.dto.dashboard.FailureProneEquipmentResponse;
import com.fixtrace.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard Analytics", description = "Endpoints for maintenance statistics, KPIs, and chart data")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get overall maintenance summary KPIs", description = "Admin only. Returns asset counts, issue breakdown, and average resolution time.")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
        DashboardSummaryResponse response = dashboardService.getSummary();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/issues-by-status")
    @Operation(summary = "Get issues grouped by status", description = "Admin only. Returns distribution of issues by lifecycle status for pie/bar charts.")
    public ResponseEntity<ApiResponse<List<ChartItemResponse>>> getIssuesByStatus() {
        List<ChartItemResponse> response = dashboardService.getIssuesByStatus();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/issues-by-priority")
    @Operation(summary = "Get issues grouped by priority", description = "Admin only. Returns distribution of issues by priority level for charts.")
    public ResponseEntity<ApiResponse<List<ChartItemResponse>>> getIssuesByPriority() {
        List<ChartItemResponse> response = dashboardService.getIssuesByPriority();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/issues-by-lab")
    @Operation(summary = "Get issues grouped by lab", description = "Admin only. Returns failure counts across all laboratories.")
    public ResponseEntity<ApiResponse<List<ChartItemResponse>>> getIssuesByLab() {
        List<ChartItemResponse> response = dashboardService.getIssuesByLab();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/failure-prone-equipment")
    @Operation(summary = "Get frequently failing equipment", description = "Admin only. Lists equipment with the highest historical failure counts.")
    public ResponseEntity<ApiResponse<List<FailureProneEquipmentResponse>>> getFailureProneEquipment(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<FailureProneEquipmentResponse> response = dashboardService.getFailureProneEquipment(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
