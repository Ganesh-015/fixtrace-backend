package com.fixtrace.service.impl;

import com.fixtrace.dto.repair.RepairRecordRequest;
import com.fixtrace.dto.repair.RepairRecordResponse;
import com.fixtrace.entity.Issue;
import com.fixtrace.entity.RepairRecord;
import com.fixtrace.entity.User;
import com.fixtrace.enums.IssueStatus;
import com.fixtrace.enums.Role;
import com.fixtrace.exception.BadRequestException;
import com.fixtrace.exception.ForbiddenException;
import com.fixtrace.exception.ResourceNotFoundException;
import com.fixtrace.mapper.RepairRecordMapper;
import com.fixtrace.repository.IssueRepository;
import com.fixtrace.repository.RepairRecordRepository;
import com.fixtrace.repository.UserRepository;
import com.fixtrace.service.RepairRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepairRecordServiceImpl implements RepairRecordService {

    private static final Logger log = LoggerFactory.getLogger(RepairRecordServiceImpl.class);

    private final RepairRecordRepository repairRecordRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final RepairRecordMapper repairRecordMapper;

    public RepairRecordServiceImpl(
            RepairRecordRepository repairRecordRepository,
            IssueRepository issueRepository,
            UserRepository userRepository,
            RepairRecordMapper repairRecordMapper
    ) {
        this.repairRecordRepository = repairRecordRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.repairRecordMapper = repairRecordMapper;
    }

    @Override
    @Transactional
    public RepairRecordResponse createRepairRecord(Long issueId, RepairRecordRequest request, String currentUserEmail) {
        log.info("Creating repair record for issue ID {} by user {}", issueId, currentUserEmail);

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));

        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.TECHNICIAN) {
            throw new ForbiddenException("Only technicians or administrators can record repair activities.");
        }

        if (currentUser.getRole() == Role.TECHNICIAN) {
            if (issue.getAssignedTechnician() == null || !issue.getAssignedTechnician().getId().equals(currentUser.getId())) {
                throw new ForbiddenException("You can only add repair records to issues assigned to you.");
            }
        }

        if (issue.getStatus() == IssueStatus.CLOSED || issue.getStatus() == IssueStatus.REJECTED) {
            throw new BadRequestException("Cannot add repair records to a " + issue.getStatus() + " issue.");
        }

        RepairRecord record = new RepairRecord();
        record.setIssue(issue);
        record.setTechnician(currentUser);
        record.setDiagnosis(request.getDiagnosis().trim());
        record.setActionTaken(request.getActionTaken().trim());
        record.setPartsUsed(request.getPartsUsed());
        record.setRepairNotes(request.getRepairNotes());
        record.setRepairStartedAt(request.getRepairStartedAt() != null ? request.getRepairStartedAt() : LocalDateTime.now());
        record.setRepairCompletedAt(request.getRepairCompletedAt());

        RepairRecord saved = repairRecordRepository.save(record);
        log.info("Repair record created with ID {} for issue {}", saved.getId(), issue.getIssueCode());

        return repairRecordMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairRecordResponse> getRepairRecordsForIssue(Long issueId) {
        if (!issueRepository.existsById(issueId)) {
            throw new ResourceNotFoundException("Issue", "id", issueId);
        }

        return repairRecordRepository.findByIssueIdOrderByCreatedAtDesc(issueId).stream()
                .map(repairRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RepairRecordResponse getRepairRecordById(Long id) {
        RepairRecord record = repairRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepairRecord", "id", id));
        return repairRecordMapper.toResponse(record);
    }

    @Override
    @Transactional
    public RepairRecordResponse updateRepairRecord(Long id, RepairRecordRequest request, String currentUserEmail) {
        log.info("Updating repair record ID {} by user {}", id, currentUserEmail);

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        RepairRecord record = repairRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepairRecord", "id", id));

        // Rule: A technician cannot modify another technician's repair record unless the user is ADMIN
        if (currentUser.getRole() != Role.ADMIN && !record.getTechnician().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You cannot modify another technician's repair record.");
        }

        if (request.getDiagnosis() != null && !request.getDiagnosis().isBlank()) {
            record.setDiagnosis(request.getDiagnosis().trim());
        }
        if (request.getActionTaken() != null && !request.getActionTaken().isBlank()) {
            record.setActionTaken(request.getActionTaken().trim());
        }
        if (request.getPartsUsed() != null) {
            record.setPartsUsed(request.getPartsUsed());
        }
        if (request.getRepairNotes() != null) {
            record.setRepairNotes(request.getRepairNotes());
        }
        if (request.getRepairStartedAt() != null) {
            record.setRepairStartedAt(request.getRepairStartedAt());
        }
        if (request.getRepairCompletedAt() != null) {
            record.setRepairCompletedAt(request.getRepairCompletedAt());
        }

        RepairRecord saved = repairRecordRepository.save(record);
        log.info("Repair record updated successfully: {}", saved.getId());

        return repairRecordMapper.toResponse(saved);
    }
}
