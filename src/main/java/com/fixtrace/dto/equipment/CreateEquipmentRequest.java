package com.fixtrace.dto.equipment;

import com.fixtrace.enums.EquipmentCategory;
import com.fixtrace.enums.EquipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class CreateEquipmentRequest {

    @NotBlank(message = "Asset code is required")
    @Size(min = 2, max = 50, message = "Asset code must be between 2 and 50 characters")
    private String assetCode;

    @NotBlank(message = "Equipment name is required")
    @Size(min = 2, max = 150, message = "Equipment name must be between 2 and 150 characters")
    private String name;

    @NotNull(message = "Category is required")
    private EquipmentCategory category;

    private String brand;

    private String model;

    @NotBlank(message = "Serial number is required")
    @Size(min = 2, max = 100, message = "Serial number must be between 2 and 100 characters")
    private String serialNumber;

    private LocalDate purchaseDate;

    private LocalDate warrantyExpiryDate;

    private EquipmentStatus status = EquipmentStatus.WORKING;

    @NotNull(message = "Lab ID is required")
    private Long labId;

    private String description;

    public CreateEquipmentRequest() {
    }

    public CreateEquipmentRequest(String assetCode, String name, EquipmentCategory category, String brand,
                                  String model, String serialNumber, LocalDate purchaseDate,
                                  LocalDate warrantyExpiryDate, EquipmentStatus status, Long labId, String description) {
        this.assetCode = assetCode;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.purchaseDate = purchaseDate;
        this.warrantyExpiryDate = warrantyExpiryDate;
        this.status = status != null ? status : EquipmentStatus.WORKING;
        this.labId = labId;
        this.description = description;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
