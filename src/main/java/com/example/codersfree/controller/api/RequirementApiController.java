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
        
        List<Requirement> requirements = course.getRequirements().stream()
                    .sorted(Comparator.comparing(Requirement::getId))
                    .toList();

        return ResponseEntity.ok(requirements);
    }

    @PostMapping("/{slug}/requirements")
    public ResponseEntity<?> createRequirement(
            @PathVariable String slug,
            @Valid @RequestBody RequirementDto requirementDto) {

        Course course = courseService.findBySlug(slug);
        Requirement requirement = requirementService.createRequirement(course, requirementDto);

        return ResponseEntity.status(201).body(requirement);
    }

    @PutMapping("/{slug}/requirements")
    public List<Requirement> updateRequirements(
            @PathVariable String slug,
            @RequestBody List<@Valid Requirement> requirements) {

        Course course = courseService.findBySlug(slug);

        return requirementService.updateRequirements(course, requirements);
    }

    @DeleteMapping("/requirements/{requirementId}")
    public ResponseEntity<?> deleteRequirement(@PathVariable Long requirementId) {
        requirementService.deleteRequirement(requirementId);
        return ResponseEntity.noContent().build();
    }
}