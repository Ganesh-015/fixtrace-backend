package com.fixtrace.service.impl;

import com.fixtrace.dto.dashboard.ChartItemResponse;
import com.fixtrace.dto.dashboard.DashboardSummaryResponse;
import com.fixtrace.dto.dashboard.FailureProneEquipmentResponse;
import com.fixtrace.entity.Equipment;
import com.fixtrace.entity.Issue;
import com.fixtrace.entity.Lab;
import com.fixtrace.enums.EquipmentStatus;
import com.fixtrace.enums.IssuePriority;
import com.fixtrace.enums.IssueStatus;
import com.fixtrace.repository.EquipmentRepository;
import com.fixtrace.repository.IssueRepository;
import com.fixtrace.repository.LabRepository;
import com.fixtrace.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EquipmentRepository equipmentRepository;
    private final IssueRepository issueRepository;
    private final LabRepository labRepository;

    public DashboardServiceImpl(
            EquipmentRepository equipmentRepository,
            IssueRepository issueRepository,
            LabRepository labRepository
    ) {
        this.equipmentRepository = equipmentRepository;
        this.issueRepository = issueRepository;
        this.labRepository = labRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        long totalEquipment = equipmentRepository.count();
        long workingEquipment = equipmentRepository.countByStatus(EquipmentStatus.WORKING);
        long underRepairEquipment = equipmentRepository.countByStatus(EquipmentStatus.UNDER_REPAIR);
        long damagedEquipment = equipmentRepository.countByStatus(EquipmentStatus.DAMAGED);
        long retiredEquipment = equipmentRepository.countByStatus(EquipmentStatus.RETIRED);

        long totalIssues = issueRepository.count();
        long reported = issueRepository.countByStatus(IssueStatus.REPORTED);
        long acknowledged = issueRepository.countByStatus(IssueStatus.ACKNOWLEDGED);
        long assigned = issueRepository.countByStatus(IssueStatus.ASSIGNED);
        long inProgress = issueRepository.countByStatus(IssueStatus.IN_PROGRESS);
        long waitingForParts = issueRepository.countByStatus(IssueStatus.WAITING_FOR_PARTS);
        long resolvedIssues = issueRepository.countByStatus(IssueStatus.RESOLVED);
        long closedIssues = issueRepository.countByStatus(IssueStatus.CLOSED);

        long openIssues = reported + acknowledged + assigned + inProgress + waitingForParts;
        long criticalIssues = issueRepository.countByPriority(IssuePriority.CRITICAL);

        // Calculate average resolution time
        List<Issue> allIssues = issueRepository.findAll();
        double totalResolutionMinutes = 0;
        int resolvedCount = 0;

        for (Issue issue : allIssues) {
            if (issue.getReportedAt() != null && issue.getResolvedAt() != null) {
                totalResolutionMinutes += Duration.between(issue.getReportedAt(), issue.getResolvedAt()).toMinutes();
                resolvedCount++;
            }
        }

        double averageHours = 0.0;
        if (resolvedCount > 0) {
            averageHours = Math.round((totalResolutionMinutes / resolvedCount / 60.0) * 10.0) / 10.0;
        }

        return new DashboardSummaryResponse(
                totalEquipment,
                workingEquipment,
                underRepairEquipment,
                damagedEquipment,
                retiredEquipment,
                totalIssues,
                openIssues,
                resolvedIssues,
                closedIssues,
                criticalIssues,
                averageHours
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChartItemResponse> getIssuesByStatus() {
        long total = issueRepository.count();
        List<ChartItemResponse> items = new ArrayList<>();

        for (IssueStatus status : IssueStatus.values()) {
            long count = issueRepository.countByStatus(status);
            double percentage = total > 0 ? Math.round(((double) count / total * 100.0) * 10.0) / 10.0 : 0.0;
            items.add(new ChartItemResponse(status.name(), count, percentage));
        }

        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChartItemResponse> getIssuesByPriority() {
        long total = issueRepository.count();
        List<ChartItemResponse> items = new ArrayList<>();

        for (IssuePriority priority : IssuePriority.values()) {
            long count = issueRepository.countByPriority(priority);
            double percentage = total > 0 ? Math.round(((double) count / total * 100.0) * 10.0) / 10.0 : 0.0;
            items.add(new ChartItemResponse(priority.name(), count, percentage));
        }

        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChartItemResponse> getIssuesByLab() {
        long total = issueRepository.count();
        List<Lab> labs = labRepository.findAll();
        List<ChartItemResponse> items = new ArrayList<>();

        for (Lab lab : labs) {
            long count = issueRepository.countByLabId(lab.getId());
            double percentage = total > 0 ? Math.round(((double) count / total * 100.0) * 10.0) / 10.0 : 0.0;
            items.add(new ChartItemResponse(lab.getLabCode() + " - " + lab.getName(), count, percentage));
        }

        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FailureProneEquipmentResponse> getFailureProneEquipment(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 50));
        List<Equipment> allEquipment = equipmentRepository.findAll();

        return allEquipment.stream()
                .map(eq -> {
                    long failureCount = issueRepository.countByEquipmentId(eq.getId());
                    String labCode = eq.getLab() != null ? eq.getLab().getLabCode() : "N/A";
                    String labName = eq.getLab() != null ? eq.getLab().getName() : "N/A";
                    return new FailureProneEquipmentResponse(
                            eq.getId(),
                            eq.getAssetCode(),
                            eq.getName(),
                            eq.getCategory(),
                            labCode,
                            labName,
                            failureCount,
                            eq.getStatus()
                    );
                })
                .sorted((a, b) -> Long.compare(b.getFailureCount(), a.getFailureCount()))
                .limit(safeLimit)
                .collect(Collectors.toList());
    }
}
