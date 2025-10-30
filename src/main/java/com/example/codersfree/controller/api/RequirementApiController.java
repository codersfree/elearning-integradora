package com.example.codersfree.controller.api;

import com.example.codersfree.dto.RequirementDto;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Requirement;
import com.example.codersfree.service.CourseService;
import com.example.codersfree.service.RequirementService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class RequirementApiController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private RequirementService requirementService;

    @GetMapping("/{slug}/requirements")
    public ResponseEntity<?> getRequirement(@PathVariable String slug) {

        Course course = courseService.findBySlug(slug);
        
        List<RequirementDto> requirements = course.getRequirements().stream()
                    .sorted(Comparator.comparing(Requirement::getId))
                    .map(r -> new RequirementDto(r.getId(), r.getName()))
                    .collect(Collectors.toList());

        return ResponseEntity.ok(requirements);
    }

    @PostMapping("/{slug}/requirements")
    public ResponseEntity<?> createRequirement(
            @PathVariable String slug,
            @Valid @RequestBody RequirementDto requirementDto) {

        Course course = courseService.findBySlug(slug);
        Requirement requirement = requirementService.createRequirement(course, requirementDto);

        RequirementDto created = new RequirementDto(requirement.getId(), requirement.getName());
        
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{slug}/requirements")
    public ResponseEntity<?> updateRequirements(
            @PathVariable String slug,
            @RequestBody List<@Valid RequirementDto> requirements) {

        Course course = courseService.findBySlug(slug);
        requirementService.updateRequirements(course, requirements);

        return ResponseEntity.ok(Map.of("message", "Requerimientos actualizados correctamente."));
    }

    @DeleteMapping("/requirements/{requirementId}")
    public ResponseEntity<?> deleteRequirement(@PathVariable Long requirementId) {
        requirementService.deleteRequirement(requirementId);
        return ResponseEntity.noContent().build();
    }
}