package com.fixtrace.service.impl;

import com.fixtrace.dto.PageResponse;
import com.fixtrace.dto.issue.*;
import com.fixtrace.entity.Equipment;
import com.fixtrace.entity.Issue;
import com.fixtrace.entity.User;
import com.fixtrace.enums.*;
import com.fixtrace.exception.BadRequestException;
import com.fixtrace.exception.ForbiddenException;
import com.fixtrace.exception.InvalidStatusTransitionException;
import com.fixtrace.exception.ResourceNotFoundException;
import com.fixtrace.mapper.IssueMapper;
import com.fixtrace.repository.EquipmentRepository;
import com.fixtrace.repository.IssueRepository;
import com.fixtrace.repository.UserRepository;
import com.fixtrace.service.IssueService;
import com.fixtrace.service.NotificationService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueServiceImpl implements IssueService {

    private static final Logger log = LoggerFactory.getLogger(IssueServiceImpl.class);

    private final IssueRepository issueRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final IssueMapper issueMapper;

    public IssueServiceImpl(
            IssueRepository issueRepository,
            EquipmentRepository equipmentRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            IssueMapper issueMapper
    ) {
        this.issueRepository = issueRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.issueMapper = issueMapper;
    }

    private synchronized String generateIssueCode() {
        int year = Year.now().getValue();
        long count = issueRepository.count() + 1;
        String formatted = String.format("FT-%d-%06d", year, count);
        // Ensure uniqueness in edge cases
        while (issueRepository.existsByIssueCode(formatted)) {
            count++;
            formatted = String.format("FT-%d-%06d", year, count);
        }
        return formatted;
    }

    @Override
    @Transactional
    public IssueResponse reportIssue(CreateIssueRequest request, String currentUserEmail) {
        log.info("Reporting issue for equipment ID {} by user {}", request.getEquipmentId(), currentUserEmail);

        User reporter = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", "id", request.getEquipmentId()));

        if (equipment.getStatus() == EquipmentStatus.RETIRED) {
            throw new BadRequestException("Cannot report an issue for retired equipment.");
        }

        Issue issue = new Issue();
        issue.setIssueCode(generateIssueCode());
        issue.setTitle(request.getTitle().trim());
        issue.setDescription(request.getDescription().trim());
        issue.setReportedBy(reporter);
        issue.setEquipment(equipment);
        issue.setLab(equipment.getLab());
        issue.setPriority(request.getPriority() != null ? request.getPriority() : IssuePriority.MEDIUM);
        issue.setStatus(IssueStatus.REPORTED);
        issue.setReportedAt(LocalDateTime.now());

        // Update equipment status if it was WORKING
        if (equipment.getStatus() == EquipmentStatus.WORKING) {
            if (issue.getPriority() == IssuePriority.CRITICAL || issue.getPriority() == IssuePriority.HIGH) {
                equipment.setStatus(EquipmentStatus.UNDER_REPAIR);
            } else {
                equipment.setStatus(EquipmentStatus.DAMAGED);
            }
            equipmentRepository.save(equipment);
        }

        Issue savedIssue = issueRepository.save(issue);
        log.info("Issue created successfully: {} with code {}", savedIssue.getId(), savedIssue.getIssueCode());

        // Notify Admins
        List<User> admins = userRepository.findByRoleAndActiveTrue(Role.ADMIN);
        for (User admin : admins) {
            notificationService.createNotification(
                    admin,
                    "New Issue Reported: " + savedIssue.getIssueCode(),
                    String.format("Issue '%s' reported on equipment %s in lab %s",
                            savedIssue.getTitle(), equipment.getAssetCode(), equipment.getLab().getLabCode()),
                    NotificationType.SYSTEM
            );
        }

        return issueMapper.toResponse(savedIssue);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<IssueResponse> getIssues(
            IssueStatus status,
            IssuePriority priority,
            Long labId,
            Long technicianId,
            String search,
            Pageable pageable,
            String currentUserEmail
    ) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        Specification<Issue> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Role-based visibility enforcement: Students and Staff only see their reported issues
            if (currentUser.getRole() == Role.STUDENT || currentUser.getRole() == Role.STAFF) {
                predicates.add(cb.equal(root.get("reportedBy").get("id"), currentUser.getId()));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            if (labId != null) {
                predicates.add(cb.equal(root.get("lab").get("id"), labId));
            }

            if (technicianId != null) {
                predicates.add(cb.equal(root.get("assignedTechnician").get("id"), technicianId));
            }

            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate codeMatch = cb.like(cb.lower(root.get("issueCode")), searchPattern);
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), searchPattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(codeMatch, titleMatch, descMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Issue> issuePage = issueRepository.findAll(spec, pageable);
        List<IssueResponse> content = issuePage.getContent().stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(issuePage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public IssueResponse getIssueById(Long id, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", id));

        // Authorization check for Students and Staff
        if ((currentUser.getRole() == Role.STUDENT || currentUser.getRole() == Role.STAFF)
                && !issue.getReportedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only view issues reported by yourself.");
        }

        return issueMapper.toResponse(issue);
    }

    @Override
    @Transactional
    public IssueResponse assignIssue(Long issueId, AssignIssueRequest request) {
        log.info("Assigning issue ID {} to technician ID {}", issueId, request.getTechnicianId());

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));

        if (issue.getStatus() == IssueStatus.CLOSED || issue.getStatus() == IssueStatus.REJECTED) {
            throw new BadRequestException("Cannot assign technician to a " + issue.getStatus() + " issue.");
        }

        User technician = userRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getTechnicianId()));

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new BadRequestException("Selected user is not a technician.");
        }

        if (!technician.isActive()) {
            throw new BadRequestException("Technician account is not active.");
        }

        issue.setAssignedTechnician(technician);
        issue.setAssignedAt(LocalDateTime.now());
        issue.setStatus(IssueStatus.ASSIGNED);

        // Update equipment to UNDER_REPAIR
        Equipment equipment = issue.getEquipment();
        if (equipment.getStatus() != EquipmentStatus.UNDER_REPAIR) {
            equipment.setStatus(EquipmentStatus.UNDER_REPAIR);
            equipmentRepository.save(equipment);
        }

        Issue savedIssue = issueRepository.save(issue);
        log.info("Issue {} assigned to technician {}", issue.getIssueCode(), technician.getEmail());

        // Notify Technician
        notificationService.createNotification(
                technician,
                "New Issue Assigned: " + issue.getIssueCode(),
                String.format("You have been assigned to repair equipment %s for issue '%s'",
                        equipment.getAssetCode(), issue.getTitle()),
                NotificationType.ISSUE_ASSIGNED
        );

        // Notify Reporter
        notificationService.createNotification(
                issue.getReportedBy(),
                "Issue Assigned: " + issue.getIssueCode(),
                String.format("Your reported issue '%s' has been assigned to technician %s",
                        issue.getTitle(), technician.getName()),
                NotificationType.ISSUE_STATUS_CHANGED
        );

        return issueMapper.toResponse(savedIssue);
    }

    @Override
    @Transactional
    public IssueResponse updatePriority(Long issueId, UpdateIssuePriorityRequest request) {
        log.info("Updating priority for issue ID {} to {}", issueId, request.getPriority());

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));

        issue.setPriority(request.getPriority());
        Issue saved = issueRepository.save(issue);
        return issueMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public IssueResponse updateStatus(Long issueId, UpdateIssueStatusRequest request, String currentUserEmail) {
        log.info("Updating status of issue ID {} to {} by {}", issueId, request.getStatus(), currentUserEmail);

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));

        // Technicians can only update issues assigned to them; Admins can update any
        if (currentUser.getRole() == Role.TECHNICIAN) {
            if (issue.getAssignedTechnician() == null || !issue.getAssignedTechnician().getId().equals(currentUser.getId())) {
                throw new ForbiddenException("Technicians can only update issues assigned to themselves.");
            }
        }

        IssueStatus current = issue.getStatus();
        IssueStatus target = request.getStatus();

        if (current == target) {
            return issueMapper.toResponse(issue);
        }

        validateStatusTransition(current, target);

        issue.setStatus(target);

        Equipment equipment = issue.getEquipment();

        if (target == IssueStatus.IN_PROGRESS || target == IssueStatus.WAITING_FOR_PARTS) {
            if (equipment.getStatus() != EquipmentStatus.UNDER_REPAIR) {
                equipment.setStatus(EquipmentStatus.UNDER_REPAIR);
                equipmentRepository.save(equipment);
            }
        } else if (target == IssueStatus.RESOLVED) {
            issue.setResolvedAt(LocalDateTime.now());

            // Check if this equipment has any other open unresolved issues
            List<IssueStatus> nonResolvedStatuses = Arrays.asList(
                    IssueStatus.REPORTED, IssueStatus.ACKNOWLEDGED, IssueStatus.ASSIGNED,
                    IssueStatus.IN_PROGRESS, IssueStatus.WAITING_FOR_PARTS
            );
            boolean hasOtherOpenIssues = issueRepository.existsByEquipmentIdAndStatusNotIn(
                    equipment.getId(),
                    Arrays.asList(IssueStatus.RESOLVED, IssueStatus.CLOSED, IssueStatus.REJECTED)
            );

            if (!hasOtherOpenIssues) {
                equipment.setStatus(EquipmentStatus.WORKING);
                equipmentRepository.save(equipment);
            }

            // Notify reporter of resolution
            notificationService.createNotification(
                    issue.getReportedBy(),
                    "Issue Resolved: " + issue.getIssueCode(),
                    String.format("Your issue '%s' has been resolved.", issue.getTitle()),
                    NotificationType.ISSUE_RESOLVED
            );
        }

        Issue saved = issueRepository.save(issue);

        // Notify reporter about status changes
        if (target != IssueStatus.RESOLVED) {
            notificationService.createNotification(
                    issue.getReportedBy(),
                    "Issue Status Updated: " + issue.getIssueCode(),
                    String.format("Status of issue '%s' changed from %s to %s.", issue.getTitle(), current, target),
                    NotificationType.ISSUE_STATUS_CHANGED
            );
        }

        return issueMapper.toResponse(saved);
    }

    private void validateStatusTransition(IssueStatus current, IssueStatus target) {
        boolean valid = false;

        switch (current) {
            case REPORTED -> valid = (target == IssueStatus.ACKNOWLEDGED || target == IssueStatus.ASSIGNED || target == IssueStatus.REJECTED);
            case ACKNOWLEDGED -> valid = (target == IssueStatus.ASSIGNED || target == IssueStatus.IN_PROGRESS || target == IssueStatus.REJECTED);
            case ASSIGNED -> valid = (target == IssueStatus.IN_PROGRESS || target == IssueStatus.REJECTED);
            case IN_PROGRESS -> valid = (target == IssueStatus.WAITING_FOR_PARTS || target == IssueStatus.RESOLVED);
            case WAITING_FOR_PARTS -> valid = (target == IssueStatus.IN_PROGRESS || target == IssueStatus.RESOLVED);
            case RESOLVED -> valid = (target == IssueStatus.CLOSED);
            case CLOSED, REJECTED -> valid = false;
        }

        if (!valid) {
            throw new InvalidStatusTransitionException(current, target);
        }
    }

    @Override
    @Transactional
    public IssueResponse closeIssue(Long issueId, String currentUserEmail) {
        log.info("Closing issue ID {} by user {}", issueId, currentUserEmail);

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));

        // Permitted: Reporter or Admin
        if (currentUser.getRole() != Role.ADMIN && !issue.getReportedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only the reporter or an administrator can close this issue.");
        }

        // Rule: Closing an issue requires it to first be RESOLVED
        if (issue.getStatus() != IssueStatus.RESOLVED) {
            throw new BadRequestException("An issue must be in RESOLVED status before it can be closed. Current status: " + issue.getStatus());
        }

        issue.setStatus(IssueStatus.CLOSED);
        Issue saved = issueRepository.save(issue);
        log.info("Issue {} closed successfully", issue.getIssueCode());

        return issueMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public IssueResponse reopenIssue(Long issueId, String currentUserEmail) {
        log.info("Reopening issue ID {} by user {}", issueId, currentUserEmail);

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));

        if (currentUser.getRole() != Role.ADMIN && !issue.getReportedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only the reporter or an administrator can reopen this issue.");
        }

        if (issue.getStatus() != IssueStatus.RESOLVED && issue.getStatus() != IssueStatus.CLOSED) {
            throw new BadRequestException("Only RESOLVED or CLOSED issues can be reopened. Current status: " + issue.getStatus());
        }

        issue.setStatus(issue.getAssignedTechnician() != null ? IssueStatus.IN_PROGRESS : IssueStatus.ACKNOWLEDGED);
        issue.setResolvedAt(null);

        Equipment equipment = issue.getEquipment();
        equipment.setStatus(EquipmentStatus.UNDER_REPAIR);
        equipmentRepository.save(equipment);

        Issue saved = issueRepository.save(issue);
        log.info("Issue {} reopened successfully", issue.getIssueCode());

        if (issue.getAssignedTechnician() != null) {
            notificationService.createNotification(
                    issue.getAssignedTechnician(),
                    "Issue Reopened: " + issue.getIssueCode(),
                    String.format("Issue '%s' on %s has been reopened by %s",
                            issue.getTitle(), equipment.getAssetCode(), currentUser.getName()),
                    NotificationType.ISSUE_REOPENED
            );
        }

        return issueMapper.toResponse(saved);
    }
}
