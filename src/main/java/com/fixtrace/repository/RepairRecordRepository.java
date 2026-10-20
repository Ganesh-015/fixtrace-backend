package com.fixtrace.repository;

import com.fixtrace.entity.RepairRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairRecordRepository extends JpaRepository<RepairRecord, Long> {

    List<RepairRecord> findByIssueIdOrderByCreatedAtDesc(Long issueId);

    List<RepairRecord> findByTechnicianIdOrderByCreatedAtDesc(Long technicianId);

    List<RepairRecord> findByIssueEquipmentIdOrderByCreatedAtDesc(Long equipmentId);
}
