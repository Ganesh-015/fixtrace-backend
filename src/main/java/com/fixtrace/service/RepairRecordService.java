package com.fixtrace.service;

import com.fixtrace.dto.repair.RepairRecordRequest;
import com.fixtrace.dto.repair.RepairRecordResponse;

import java.util.List;

public interface RepairRecordService {

    RepairRecordResponse createRepairRecord(Long issueId, RepairRecordRequest request, String currentUserEmail);

    List<RepairRecordResponse> getRepairRecordsForIssue(Long issueId);

    RepairRecordResponse getRepairRecordById(Long id);

    RepairRecordResponse updateRepairRecord(Long id, RepairRecordRequest request, String currentUserEmail);
}
