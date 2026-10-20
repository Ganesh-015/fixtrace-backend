package com.fixtrace.service;

import com.fixtrace.dto.dashboard.ChartItemResponse;
import com.fixtrace.dto.dashboard.DashboardSummaryResponse;
import com.fixtrace.dto.dashboard.FailureProneEquipmentResponse;

import java.util.List;

public interface DashboardService {

    DashboardSummaryResponse getSummary();

    List<ChartItemResponse> getIssuesByStatus();

    List<ChartItemResponse> getIssuesByPriority();

    List<ChartItemResponse> getIssuesByLab();

    List<FailureProneEquipmentResponse> getFailureProneEquipment(int limit);
}
