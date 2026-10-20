package com.fixtrace.exception;

import com.fixtrace.enums.IssueStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }

    public InvalidStatusTransitionException(IssueStatus currentStatus, IssueStatus targetStatus) {
        super(String.format("Invalid status transition from '%s' to '%s'.", currentStatus, targetStatus));
    }
}
