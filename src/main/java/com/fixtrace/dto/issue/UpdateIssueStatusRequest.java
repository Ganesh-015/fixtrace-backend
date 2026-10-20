package com.fixtrace.dto.issue;

import com.fixtrace.enums.IssueStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateIssueStatusRequest {

    @NotNull(message = "Status is required")
    private IssueStatus status;

    private String comment;

    public UpdateIssueStatusRequest() {
    }

    public UpdateIssueStatusRequest(IssueStatus status, String comment) {
        this.status = status;
        this.comment = comment;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
