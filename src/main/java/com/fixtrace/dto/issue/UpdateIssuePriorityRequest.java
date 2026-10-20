package com.fixtrace.dto.issue;

import com.fixtrace.enums.IssuePriority;
import jakarta.validation.constraints.NotNull;

public class UpdateIssuePriorityRequest {

    @NotNull(message = "Priority is required")
    private IssuePriority priority;

    public UpdateIssuePriorityRequest() {
    }

    public UpdateIssuePriorityRequest(IssuePriority priority) {
        this.priority = priority;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public void setPriority(IssuePriority priority) {
        this.priority = priority;
    }
}
