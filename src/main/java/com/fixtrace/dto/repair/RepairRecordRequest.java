package com.fixtrace.dto.repair;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class RepairRecordRequest {

    @NotBlank(message = "Diagnosis is required")
    @Size(max = 2000, message = "Diagnosis cannot exceed 2000 characters")
    private String diagnosis;

    @NotBlank(message = "Action taken is required")
    @Size(max = 2000, message = "Action taken cannot exceed 2000 characters")
    private String actionTaken;

    @Size(max = 1000, message = "Parts used description cannot exceed 1000 characters")
    private String partsUsed;

    @Size(max = 2000, message = "Repair notes cannot exceed 2000 characters")
    private String repairNotes;

    private LocalDateTime repairStartedAt;

    private LocalDateTime repairCompletedAt;

    public RepairRecordRequest() {
    }

    public RepairRecordRequest(String diagnosis, String actionTaken, String partsUsed, String repairNotes,
                               LocalDateTime repairStartedAt, LocalDateTime repairCompletedAt) {
        this.diagnosis = diagnosis;
        this.actionTaken = actionTaken;
        this.partsUsed = partsUsed;
        this.repairNotes = repairNotes;
        this.repairStartedAt = repairStartedAt;
        this.repairCompletedAt = repairCompletedAt;
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
