package com.fixtrace.dto.repair;

import java.time.LocalDateTime;

public class RepairRecordResponse {

    private Long id;
    private Long issueId;
    private String issueCode;
    private String equipmentAssetCode;
    private Long technicianId;
    private String technicianName;
    private String technicianEmail;
    private String diagnosis;
    private String actionTaken;
    private String partsUsed;
    private String repairNotes;
    private LocalDateTime repairStartedAt;
    private LocalDateTime repairCompletedAt;
    private LocalDateTime createdAt;

    public RepairRecordResponse() {
    }

    public RepairRecordResponse(Long id, Long issueId, String issueCode, String equipmentAssetCode,
                                Long technicianId, String technicianName, String technicianEmail,
                                String diagnosis, String actionTaken, String partsUsed, String repairNotes,
                                LocalDateTime repairStartedAt, LocalDateTime repairCompletedAt, LocalDateTime createdAt) {
        this.id = id;
        this.issueId = issueId;
        this.issueCode = issueCode;
        this.equipmentAssetCode = equipmentAssetCode;
        this.technicianId = technicianId;
        this.technicianName = technicianName;
        this.technicianEmail = technicianEmail;
        this.diagnosis = diagnosis;
        this.actionTaken = actionTaken;
        this.partsUsed = partsUsed;
        this.repairNotes = repairNotes;
        this.repairStartedAt = repairStartedAt;
        this.repairCompletedAt = repairCompletedAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEquipmentAssetCode() {
        return equipmentAssetCode;
    }

    public void setEquipmentAssetCode(String equipmentAssetCode) {
        this.equipmentAssetCode = equipmentAssetCode;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getTechnicianEmail() {
        return technicianEmail;
    }

    public void setTechnicianEmail(String technicianEmail) {
        this.technicianEmail = technicianEmail;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
