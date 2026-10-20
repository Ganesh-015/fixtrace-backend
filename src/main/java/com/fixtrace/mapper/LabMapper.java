package com.fixtrace.mapper;

import com.fixtrace.dto.lab.LabResponse;
import com.fixtrace.entity.Lab;
import org.springframework.stereotype.Component;

@Component
public class LabMapper {

    public LabResponse toResponse(Lab lab, long equipmentCount) {
        if (lab == null) {
            return null;
        }
        return new LabResponse(
                lab.getId(),
                lab.getLabCode(),
                lab.getName(),
                lab.getBuilding(),
                lab.getFloor(),
                lab.getRoomNumber(),
                lab.getDescription(),
                lab.isActive(),
                equipmentCount,
                lab.getCreatedAt(),
                lab.getUpdatedAt()
        );
    }

    public LabResponse toResponse(Lab lab) {
        return toResponse(lab, 0);
    }
}
