package com.fixtrace.dto.issue;

import jakarta.validation.constraints.NotNull;

public class AssignIssueRequest {

    @NotNull(message = "Technician ID is required")
    private Long technicianId;

    public AssignIssueRequest() {
    }

    public AssignIssueRequest(Long technicianId) {
        this.technicianId = technicianId;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }
}
