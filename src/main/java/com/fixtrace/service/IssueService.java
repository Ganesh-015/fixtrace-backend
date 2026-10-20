package com.fixtrace.service;

import com.fixtrace.dto.PageResponse;
import com.fixtrace.dto.issue.*;
import com.fixtrace.enums.IssuePriority;
import com.fixtrace.enums.IssueStatus;
import org.springframework.data.domain.Pageable;

public interface IssueService {

    IssueResponse reportIssue(CreateIssueRequest request, String currentUserEmail);

    PageResponse<IssueResponse> getIssues(
            IssueStatus status,
            IssuePriority priority,
            Long labId,
            Long technicianId,
            String search,
            Pageable pageable,
            String currentUserEmail
    );

    IssueResponse getIssueById(Long id, String currentUserEmail);

    IssueResponse assignIssue(Long issueId, AssignIssueRequest request);

    IssueResponse updatePriority(Long issueId, UpdateIssuePriorityRequest request);

    IssueResponse updateStatus(Long issueId, UpdateIssueStatusRequest request, String currentUserEmail);

    IssueResponse closeIssue(Long issueId, String currentUserEmail);

    IssueResponse reopenIssue(Long issueId, String currentUserEmail);
}
