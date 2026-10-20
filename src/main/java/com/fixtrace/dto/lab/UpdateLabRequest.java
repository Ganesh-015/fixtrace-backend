package com.fixtrace.dto.lab;

import jakarta.validation.constraints.Size;

public class UpdateLabRequest {

    @Size(min = 2, max = 100, message = "Lab name must be between 2 and 100 characters")
    private String name;

    @Size(min = 1, max = 100, message = "Building name must be between 1 and 100 characters")
    private String building;

    private String floor;

    @Size(min = 1, max = 50, message = "Room number must be between 1 and 50 characters")
    private String roomNumber;

    private String description;

    private Boolean active;

    public UpdateLabRequest() {
    }

    public UpdateLabRequest(String name, String building, String floor, String roomNumber, String description, Boolean active) {
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.roomNumber = roomNumber;
        this.description = description;
        this.active = active;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
