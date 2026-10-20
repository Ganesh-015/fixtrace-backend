package com.fixtrace.mapper;

import com.fixtrace.dto.equipment.EquipmentResponse;
import com.fixtrace.entity.Equipment;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapper {

    public EquipmentResponse toResponse(Equipment equipment) {
        if (equipment == null) {
            return null;
        }

        Long labId = equipment.getLab() != null ? equipment.getLab().getId() : null;
        String labCode = equipment.getLab() != null ? equipment.getLab().getLabCode() : null;
        String labName = equipment.getLab() != null ? equipment.getLab().getName() : null;
        String building = equipment.getLab() != null ? equipment.getLab().getBuilding() : null;
        String roomNumber = equipment.getLab() != null ? equipment.getLab().getRoomNumber() : null;

        return new EquipmentResponse(
                equipment.getId(),
                equipment.getAssetCode(),
                equipment.getName(),
                equipment.getCategory(),
                equipment.getBrand(),
                equipment.getModel(),
                equipment.getSerialNumber(),
                equipment.getPurchaseDate(),
                equipment.getWarrantyExpiryDate(),
                equipment.getStatus(),
                labId,
                labCode,
                labName,
                building,
                roomNumber,
                equipment.getDescription(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt()
        );
    }
}
