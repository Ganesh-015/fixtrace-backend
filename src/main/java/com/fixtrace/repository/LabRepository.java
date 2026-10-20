package com.fixtrace.repository;

import com.fixtrace.entity.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabRepository extends JpaRepository<Lab, Long> {

    Optional<Lab> findByLabCode(String labCode);

    boolean existsByLabCode(String labCode);

    List<Lab> findByActiveTrue();
}
