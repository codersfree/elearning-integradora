package com.example.codersfree.service;

import com.example.codersfree.dto.RequirementDto;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Requirement;
import com.example.codersfree.repository.RequirementRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Requerimiento no encontrado con ID: " + id));
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
    public List<Requirement> updateRequirements(Course course, List<Requirement> requirementsInput) {

        Map<Long, Requirement> existingRequirements = requirementRepository.findAllById(
                requirementsInput.stream().map(Requirement::getId).toList()).stream()
                .collect(Collectors.toMap(Requirement::getId, Function.identity()));

        requirementsInput.forEach(inputReq -> {
            Requirement managedReq = existingRequirements.get(inputReq.getId());
            if (managedReq != null) {
                String newName = inputReq.getName();
                if (newName != null && !newName.isBlank() && !newName.equals(managedReq.getName())) {
                    managedReq.setName(newName);
                }
            }
        });

        return requirementRepository.saveAll(existingRequirements.values());

    }

    @Transactional
    public void deleteRequirement(Long requirementId) {
        if (!requirementRepository.existsById(requirementId)) {
            throw new EntityNotFoundException("Requerimiento no encontrado con ID: " + requirementId);
        }
        requirementRepository.deleteById(requirementId);
    }
}