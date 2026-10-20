package com.fixtrace.repository;

import com.fixtrace.entity.Issue;
import com.fixtrace.enums.IssuePriority;
import com.fixtrace.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    Optional<Issue> findByIssueCode(String issueCode);

    boolean existsByIssueCode(String issueCode);

    List<Issue> findByEquipmentId(Long equipmentId);

    List<Issue> findByEquipmentIdOrderByReportedAtDesc(Long equipmentId);

    List<Issue> findByReportedById(Long reportedById);

    List<Issue> findByAssignedTechnicianId(Long technicianId);

    long countByStatus(IssueStatus status);

    long countByPriority(IssuePriority priority);

    long countByLabId(Long labId);

    long countByEquipmentId(Long equipmentId);

    boolean existsByEquipmentIdAndStatusNotIn(Long equipmentId, Collection<IssueStatus> statuses);
}
