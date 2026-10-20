package com.fixtrace.mapper;

import com.fixtrace.dto.repair.RepairRecordResponse;
import com.fixtrace.entity.RepairRecord;
import org.springframework.stereotype.Component;

@Component
public class RepairRecordMapper {

    public RepairRecordResponse toResponse(RepairRecord record) {
        if (record == null) {
            return null;
        }

        Long issueId = record.getIssue() != null ? record.getIssue().getId() : null;
        String issueCode = record.getIssue() != null ? record.getIssue().getIssueCode() : null;
        String assetCode = (record.getIssue() != null && record.getIssue().getEquipment() != null)
                ? record.getIssue().getEquipment().getAssetCode() : null;

        Long techId = record.getTechnician() != null ? record.getTechnician().getId() : null;
        String techName = record.getTechnician() != null ? record.getTechnician().getName() : null;
        String techEmail = record.getTechnician() != null ? record.getTechnician().getEmail() : null;

        return new RepairRecordResponse(
                record.getId(),
                issueId,
                issueCode,
                assetCode,
                techId,
                techName,
                techEmail,
                record.getDiagnosis(),
                record.getActionTaken(),
                record.getPartsUsed(),
                record.getRepairNotes(),
                record.getRepairStartedAt(),
                record.getRepairCompletedAt(),
                record.getCreatedAt()
        );
    }
}
