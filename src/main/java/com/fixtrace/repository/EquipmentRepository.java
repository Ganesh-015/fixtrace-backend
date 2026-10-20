package com.fixtrace.repository;

import com.fixtrace.entity.Equipment;
import com.fixtrace.enums.EquipmentCategory;
import com.fixtrace.enums.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long>, JpaSpecificationExecutor<Equipment> {

    Optional<Equipment> findByAssetCode(String assetCode);

    Optional<Equipment> findBySerialNumber(String serialNumber);

    boolean existsByAssetCode(String assetCode);

    boolean existsBySerialNumber(String serialNumber);

    List<Equipment> findByLabId(Long labId);

    long countByStatus(EquipmentStatus status);

    long countByCategory(EquipmentCategory category);

    long countByLabId(Long labId);
}
