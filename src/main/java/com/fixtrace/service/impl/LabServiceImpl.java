package com.fixtrace.service.impl;

import com.fixtrace.dto.equipment.EquipmentResponse;
import com.fixtrace.dto.lab.CreateLabRequest;
import com.fixtrace.dto.lab.LabResponse;
import com.fixtrace.dto.lab.UpdateLabRequest;
import com.fixtrace.entity.Equipment;
import com.fixtrace.entity.Lab;
import com.fixtrace.exception.DuplicateResourceException;
import com.fixtrace.exception.ResourceNotFoundException;
import com.fixtrace.mapper.EquipmentMapper;
import com.fixtrace.mapper.LabMapper;
import com.fixtrace.repository.EquipmentRepository;
import com.fixtrace.repository.LabRepository;
import com.fixtrace.service.LabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabServiceImpl implements LabService {

    private static final Logger log = LoggerFactory.getLogger(LabServiceImpl.class);

    private final LabRepository labRepository;
    private final EquipmentRepository equipmentRepository;
    private final LabMapper labMapper;
    private final EquipmentMapper equipmentMapper;

    public LabServiceImpl(
            LabRepository labRepository,
            EquipmentRepository equipmentRepository,
            LabMapper labMapper,
            EquipmentMapper equipmentMapper
    ) {
        this.labRepository = labRepository;
        this.equipmentRepository = equipmentRepository;
        this.labMapper = labMapper;
        this.equipmentMapper = equipmentMapper;
    }

    @Override
    @Transactional
    public LabResponse createLab(CreateLabRequest request) {
        log.info("Creating lab with code: {}", request.getLabCode());

        if (labRepository.existsByLabCode(request.getLabCode().trim())) {
            throw new DuplicateResourceException("Lab", "labCode", request.getLabCode());
        }

        Lab lab = new Lab();
        lab.setLabCode(request.getLabCode().trim().toUpperCase());
        lab.setName(request.getName().trim());
        lab.setBuilding(request.getBuilding().trim());
        lab.setFloor(request.getFloor());
        lab.setRoomNumber(request.getRoomNumber().trim());
        lab.setDescription(request.getDescription());
        lab.setActive(true);

        Lab savedLab = labRepository.save(lab);
        log.info("Lab created successfully with ID: {}", savedLab.getId());

        return labMapper.toResponse(savedLab, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabResponse> getAllLabs() {
        return labRepository.findAll().stream()
                .map(lab -> {
                    long count = equipmentRepository.countByLabId(lab.getId());
                    return labMapper.toResponse(lab, count);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LabResponse getLabById(Long id) {
        Lab lab = labRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab", "id", id));
        long count = equipmentRepository.countByLabId(lab.getId());
        return labMapper.toResponse(lab, count);
    }

    @Override
    @Transactional
    public LabResponse updateLab(Long id, UpdateLabRequest request) {
        log.info("Updating lab with ID: {}", id);

        Lab lab = labRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab", "id", id));

        if (request.getName() != null && !request.getName().isBlank()) {
            lab.setName(request.getName().trim());
        }
        if (request.getBuilding() != null && !request.getBuilding().isBlank()) {
            lab.setBuilding(request.getBuilding().trim());
        }
        if (request.getFloor() != null) {
            lab.setFloor(request.getFloor());
        }
        if (request.getRoomNumber() != null && !request.getRoomNumber().isBlank()) {
            lab.setRoomNumber(request.getRoomNumber().trim());
        }
        if (request.getDescription() != null) {
            lab.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            lab.setActive(request.getActive());
        }

        Lab updatedLab = labRepository.save(lab);
        long count = equipmentRepository.countByLabId(updatedLab.getId());
        log.info("Lab updated successfully: {}", updatedLab.getId());

        return labMapper.toResponse(updatedLab, count);
    }

    @Override
    @Transactional
    public void deleteLab(Long id) {
        log.info("Deactivating lab with ID: {}", id);

        Lab lab = labRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab", "id", id));

        lab.setActive(false);
        labRepository.save(lab);
        log.info("Lab deactivated successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentResponse> getLabEquipment(Long labId) {
        if (!labRepository.existsById(labId)) {
            throw new ResourceNotFoundException("Lab", "id", labId);
        }

        List<Equipment> equipmentList = equipmentRepository.findByLabId(labId);
        return equipmentList.stream()
                .map(equipmentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
