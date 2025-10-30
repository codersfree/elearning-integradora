package com.example.codersfree.service;

import com.example.codersfree.dto.RequirementDto;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Requirement;
import com.example.codersfree.repository.RequirementRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequirementService {

    @Autowired
    private RequirementRepository requirementRepository;

    @Transactional(readOnly = true)
    public Requirement findById(Long id) {
        return requirementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Requerimiento no encontrado con ID: " + id));
    }

    @Transactional
    public Requirement createRequirement(Course course, RequirementDto dto) {
        Requirement requirement = Requirement.builder()
                .name(dto.getName())
                .course(course)
                .build();

        return requirementRepository.save(requirement);
    }

    @Transactional
    public void updateRequirements(Course course, List<RequirementDto> requirementDtos) {

        Map<Long, Requirement> requirements = requirementRepository.findAllById(
                requirementDtos.stream()
                        .map(RequirementDto::getId)
                        .toList())
                .stream()
                .collect(Collectors.toMap(Requirement::getId, Function.identity()));

        for (RequirementDto dto : requirementDtos) {

            String newName = dto.getName();

            if (newName == null || newName.isBlank()) {
                continue; // ignorar nombres vacíos
            }

            Requirement requirement = requirements.get(dto.getId());

            if (!requirement.getName().equals(newName)) {
                requirement.setName(newName);
            }
        }

        requirementRepository.saveAll(requirements.values());
    }

    @Transactional
    public void deleteRequirement(Long requirementId) {
        if (!requirementRepository.existsById(requirementId)) {
            throw new EntityNotFoundException("Requerimiento no encontrado con ID: " + requirementId);
        }
        requirementRepository.deleteById(requirementId);
    }
}