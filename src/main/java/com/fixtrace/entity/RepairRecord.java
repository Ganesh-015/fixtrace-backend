package com.fixtrace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "repair_records", indexes = {
    @Index(name = "idx_repair_issue", columnList = "issue_id"),
    @Index(name = "idx_repair_tech", columnList = "technician_id")
})
public class RepairRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;

    @Column(nullable = false, length = 2000)
    private String diagnosis;

    @Column(name = "action_taken", nullable = false, length = 2000)
    private String actionTaken;

    @Column(name = "parts_used", length = 1000)
    private String partsUsed;

    @Column(name = "repair_notes", length = 2000)
    private String repairNotes;

    @Column(name = "repair_started_at")
    private LocalDateTime repairStartedAt;

    @Column(name = "repair_completed_at")
    private LocalDateTime repairCompletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public RepairRecord() {
    }

    public RepairRecord(Long id, Issue issue, User technician, String diagnosis, String actionTaken,
                        String partsUsed, String repairNotes, LocalDateTime repairStartedAt,
                        LocalDateTime repairCompletedAt) {
        this.id = id;
        this.issue = issue;
        this.technician = technician;
        this.diagnosis = diagnosis;
        this.actionTaken = actionTaken;
        this.partsUsed = partsUsed;
        this.repairNotes = repairNotes;
        this.repairStartedAt = repairStartedAt;
        this.repairCompletedAt = repairCompletedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public User getTechnician() {
        return technician;
    }

    public void setTechnician(User technician) {
        this.technician = technician;
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
