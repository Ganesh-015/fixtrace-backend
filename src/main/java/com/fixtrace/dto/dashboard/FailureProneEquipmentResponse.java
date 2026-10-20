package com.fixtrace.dto.dashboard;

import com.fixtrace.enums.EquipmentCategory;
import com.fixtrace.enums.EquipmentStatus;

public class FailureProneEquipmentResponse {

    private Long equipmentId;
    private String assetCode;
    private String equipmentName;
    private EquipmentCategory category;
    private String labCode;
    private String labName;
    private long failureCount;
    private EquipmentStatus currentStatus;

    public FailureProneEquipmentResponse() {
    }

    public FailureProneEquipmentResponse(Long equipmentId, String assetCode, String equipmentName,
                                        EquipmentCategory category, String labCode, String labName,
                                        long failureCount, EquipmentStatus currentStatus) {
        this.equipmentId = equipmentId;
        this.assetCode = assetCode;
        this.equipmentName = equipmentName;
        this.category = category;
        this.labCode = labCode;
        this.labName = labName;
        this.failureCount = failureCount;
        this.currentStatus = currentStatus;
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

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public EquipmentCategory getCategory() {
        return category;
    }

    public void setCategory(EquipmentCategory category) {
        this.category = category;
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

    public long getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(long failureCount) {
        this.failureCount = failureCount;
    }

    public EquipmentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(EquipmentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }
}
