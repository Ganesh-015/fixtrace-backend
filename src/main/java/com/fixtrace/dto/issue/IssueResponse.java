package com.fixtrace.dto.issue;

import com.fixtrace.enums.IssuePriority;
import com.fixtrace.enums.IssueStatus;
import java.time.LocalDateTime;

public class IssueResponse {

    private Long id;
    private String issueCode;
    private String title;
    private String description;
    private Long reportedById;
    private String reportedByName;
    private String reportedByEmail;
    private Long equipmentId;
    private String equipmentAssetCode;
    private String equipmentName;
    private Long labId;
    private String labCode;
    private String labName;
    private IssuePriority priority;
    private IssueStatus status;
    private Long assignedTechnicianId;
    private String assignedTechnicianName;
    private String assignedTechnicianEmail;
    private LocalDateTime reportedAt;
    private LocalDateTime assignedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime updatedAt;

    public IssueResponse() {
    }

    public IssueResponse(Long id, String issueCode, String title, String description, Long reportedById,
                         String reportedByName, String reportedByEmail, Long equipmentId, String equipmentAssetCode,
                         String equipmentName, Long labId, String labCode, String labName, IssuePriority priority,
                         IssueStatus status, Long assignedTechnicianId, String assignedTechnicianName,
                         String assignedTechnicianEmail, LocalDateTime reportedAt, LocalDateTime assignedAt,
                         LocalDateTime resolvedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.issueCode = issueCode;
        this.title = title;
        this.description = description;
        this.reportedById = reportedById;
        this.reportedByName = reportedByName;
        this.reportedByEmail = reportedByEmail;
        this.equipmentId = equipmentId;
        this.equipmentAssetCode = equipmentAssetCode;
        this.equipmentName = equipmentName;
        this.labId = labId;
        this.labCode = labCode;
        this.labName = labName;
        this.priority = priority;
        this.status = status;
        this.assignedTechnicianId = assignedTechnicianId;
        this.assignedTechnicianName = assignedTechnicianName;
        this.assignedTechnicianEmail = assignedTechnicianEmail;
        this.reportedAt = reportedAt;
        this.assignedAt = assignedAt;
        this.resolvedAt = resolvedAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getReportedById() {
        return reportedById;
    }

    public void setReportedById(Long reportedById) {
        this.reportedById = reportedById;
    }

    public String getReportedByName() {
        return reportedByName;
    }

    public void setReportedByName(String reportedByName) {
        this.reportedByName = reportedByName;
    }

    public String getReportedByEmail() {
        return reportedByEmail;
    }

    public void setReportedByEmail(String reportedByEmail) {
        this.reportedByEmail = reportedByEmail;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentAssetCode() {
        return equipmentAssetCode;
    }

    public void setEquipmentAssetCode(String equipmentAssetCode) {
        this.equipmentAssetCode = equipmentAssetCode;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
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

    public Long getAssignedTechnicianId() {
        return assignedTechnicianId;
    }

    public void setAssignedTechnicianId(Long assignedTechnicianId) {
        this.assignedTechnicianId = assignedTechnicianId;
    }

    public String getAssignedTechnicianName() {
        return assignedTechnicianName;
    }

    public void setAssignedTechnicianName(String assignedTechnicianName) {
        this.assignedTechnicianName = assignedTechnicianName;
    }

    public String getAssignedTechnicianEmail() {
        return assignedTechnicianEmail;
    }

    public void setAssignedTechnicianEmail(String assignedTechnicianEmail) {
        this.assignedTechnicianEmail = assignedTechnicianEmail;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
