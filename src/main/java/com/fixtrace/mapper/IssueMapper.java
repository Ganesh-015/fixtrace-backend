package com.fixtrace.mapper;

import com.fixtrace.dto.issue.IssueResponse;
import com.fixtrace.entity.Issue;
import org.springframework.stereotype.Component;

@Component
public class IssueMapper {

    public IssueResponse toResponse(Issue issue) {
        if (issue == null) {
            return null;
        }

        Long reportedById = issue.getReportedBy() != null ? issue.getReportedBy().getId() : null;
        String reportedByName = issue.getReportedBy() != null ? issue.getReportedBy().getName() : null;
        String reportedByEmail = issue.getReportedBy() != null ? issue.getReportedBy().getEmail() : null;

        Long equipmentId = issue.getEquipment() != null ? issue.getEquipment().getId() : null;
        String equipmentAssetCode = issue.getEquipment() != null ? issue.getEquipment().getAssetCode() : null;
        String equipmentName = issue.getEquipment() != null ? issue.getEquipment().getName() : null;

        Long labId = issue.getLab() != null ? issue.getLab().getId() : null;
        String labCode = issue.getLab() != null ? issue.getLab().getLabCode() : null;
        String labName = issue.getLab() != null ? issue.getLab().getName() : null;

        Long techId = issue.getAssignedTechnician() != null ? issue.getAssignedTechnician().getId() : null;
        String techName = issue.getAssignedTechnician() != null ? issue.getAssignedTechnician().getName() : null;
        String techEmail = issue.getAssignedTechnician() != null ? issue.getAssignedTechnician().getEmail() : null;

        return new IssueResponse(
                issue.getId(),
                issue.getIssueCode(),
                issue.getTitle(),
                issue.getDescription(),
                reportedById,
                reportedByName,
                reportedByEmail,
                equipmentId,
                equipmentAssetCode,
                equipmentName,
                labId,
                labCode,
                labName,
                issue.getPriority(),
                issue.getStatus(),
                techId,
                techName,
                techEmail,
                issue.getReportedAt(),
                issue.getAssignedAt(),
                issue.getResolvedAt(),
                issue.getUpdatedAt()
        );
    }
}
