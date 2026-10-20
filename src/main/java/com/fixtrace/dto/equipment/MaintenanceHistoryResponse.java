package com.fixtrace.dto.equipment;

import com.fixtrace.enums.EquipmentCategory;
import com.fixtrace.enums.EquipmentStatus;
import com.fixtrace.enums.IssuePriority;
import com.fixtrace.enums.IssueStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceHistoryResponse {

    private Long equipmentId;
    private String assetCode;
    private String name;
    private EquipmentCategory category;
    private EquipmentStatus status;
    private String labCode;
    private String labName;
    private int totalFailures;
    private int totalRepairs;
    private double totalDowntimeHours;
    private List<HistoricalIssueItem> issues = new ArrayList<>();

    public MaintenanceHistoryResponse() {
    }

    public static class HistoricalIssueItem {
        private Long issueId;
        private String issueCode;
        private String title;
        private String description;
        private IssuePriority priority;
        private IssueStatus status;
        private String reportedByName;
        private LocalDateTime reportedAt;
        private String technicianName;
        private LocalDateTime assignedAt;
        private LocalDateTime resolvedAt;
        private Double downtimeHours;
        private List<HistoricalRepairItem> repairRecords = new ArrayList<>();

        public HistoricalIssueItem() {
        }

        public Long getIssueId() {
            return issueId;
        }

        public void setIssueId(Long issueId) {
            this.issueId = issueId;
        }

        public String getIssueCode() {
            return issueCode;
        }

        public void setIssueCode(String issueCode) {
            this.issueCode = issueCode;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public IssuePriority getPriority() {
            return priority;
        }

        public void setPriority(IssuePriority priority) {
            this.priority = priority;
        }

        public IssueStatus getStatus() {
            return status;
        }

        public void setStatus(IssueStatus status) {
            this.status = status;
        }

        public String getReportedByName() {
            return reportedByName;
        }

        public void setReportedByName(String reportedByName) {
            this.reportedByName = reportedByName;
        }

        public LocalDateTime getReportedAt() {
            return reportedAt;
        }

        public void setReportedAt(LocalDateTime reportedAt) {
            this.reportedAt = reportedAt;
        }

        public String getTechnicianName() {
            return technicianName;
        }

        public void setTechnicianName(String technicianName) {
            this.technicianName = technicianName;
        }

        public LocalDateTime getAssignedAt() {
            return assignedAt;
        }

        public void setAssignedAt(LocalDateTime assignedAt) {
            this.assignedAt = assignedAt;
        }

        public LocalDateTime getResolvedAt() {
            return resolvedAt;
        }

        public void setResolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
        }

        public Double getDowntimeHours() {
            return downtimeHours;
        }

        public void setDowntimeHours(Double downtimeHours) {
            this.downtimeHours = downtimeHours;
        }

        public List<HistoricalRepairItem> getRepairRecords() {
            return repairRecords;
        }

        public void setRepairRecords(List<HistoricalRepairItem> repairRecords) {
            this.repairRecords = repairRecords;
        }
    }

    public static class HistoricalRepairItem {
        private Long repairId;
        private String technicianName;
        private String diagnosis;
        private String actionTaken;
        private String partsUsed;
        private String repairNotes;
        private LocalDateTime repairStartedAt;
        private LocalDateTime repairCompletedAt;

        public HistoricalRepairItem() {
        }

        public Long getRepairId() {
            return repairId;
        }

        public void setRepairId(Long repairId) {
            this.repairId = repairId;
        }

        public String getTechnicianName() {
            return technicianName;
        }

        public void setTechnicianName(String technicianName) {
            this.technicianName = technicianName;
        }

        public String getDiagnosis() {
            return diagnosis;
        }

        public void setDiagnosis(String diagnosis) {
            this.diagnosis = diagnosis;
        }

        public String getActionTaken() {
            return actionTaken;
        }

        public void setActionTaken(String actionTaken) {
            this.actionTaken = actionTaken;
        }

        public String getPartsUsed() {
            return partsUsed;
        }

        public void setPartsUsed(String partsUsed) {
            this.partsUsed = partsUsed;
        }

        public String getRepairNotes() {
            return repairNotes;
        }

        public void setRepairNotes(String repairNotes) {
            this.repairNotes = repairNotes;
        }

        public LocalDateTime getRepairStartedAt() {
            return repairStartedAt;
        }

        public void setRepairStartedAt(LocalDateTime repairStartedAt) {
            this.repairStartedAt = repairStartedAt;
        }

        public LocalDateTime getRepairCompletedAt() {
            return repairCompletedAt;
        }

        public void setRepairCompletedAt(LocalDateTime repairCompletedAt) {
            this.repairCompletedAt = repairCompletedAt;
        }
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EquipmentCategory getCategory() {
        return category;
    }

    public void setCategory(EquipmentCategory category) {
        this.category = category;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public int getTotalFailures() {
        return totalFailures;
    }

    public void setTotalFailures(int totalFailures) {
        this.totalFailures = totalFailures;
    }

    public int getTotalRepairs() {
        return totalRepairs;
    }

    public void setTotalRepairs(int totalRepairs) {
        this.totalRepairs = totalRepairs;
    }

    public double getTotalDowntimeHours() {
        return totalDowntimeHours;
    }

    public void setTotalDowntimeHours(double totalDowntimeHours) {
        this.totalDowntimeHours = totalDowntimeHours;
    }

    public List<HistoricalIssueItem> getIssues() {
        return issues;
    }

    public void setIssues(List<HistoricalIssueItem> issues) {
        this.issues = issues;
    }
}
