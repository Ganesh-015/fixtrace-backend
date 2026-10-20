package com.fixtrace.dto.lab;

import java.time.LocalDateTime;

public class LabResponse {

    private Long id;
    private String labCode;
    private String name;
    private String building;
    private String floor;
    private String roomNumber;
    private String description;
    private boolean active;
    private long equipmentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LabResponse() {
    }

    public LabResponse(Long id, String labCode, String name, String building, String floor, String roomNumber,
                       String description, boolean active, long equipmentCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.labCode = labCode;
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.roomNumber = roomNumber;
        this.description = description;
        this.active = active;
        this.equipmentCount = equipmentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getEquipmentCount() {
        return equipmentCount;
    }

    public void setEquipmentCount(long equipmentCount) {
        this.equipmentCount = equipmentCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
