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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instructor/courses")
public class RequirementApiController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private RequirementService requirementService;

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