package com.fixtrace.service;

import com.fixtrace.dto.equipment.EquipmentResponse;
import com.fixtrace.dto.lab.CreateLabRequest;
import com.fixtrace.dto.lab.LabResponse;
import com.fixtrace.dto.lab.UpdateLabRequest;

import java.util.List;

public interface LabService {

    LabResponse createLab(CreateLabRequest request);

    List<LabResponse> getAllLabs();

    LabResponse getLabById(Long id);

    LabResponse updateLab(Long id, UpdateLabRequest request);

    void deleteLab(Long id);

    List<EquipmentResponse> getLabEquipment(Long labId);
}
