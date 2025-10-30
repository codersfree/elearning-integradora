package com.example.codersfree.controller.api;

import com.example.codersfree.dto.GoalDto;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Goal;
import com.example.codersfree.service.CourseService;
import com.example.codersfree.service.GoalService;
import jakarta.validation.Valid;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class GoalApiController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private GoalService goalService;

    @GetMapping("/{slug}/goals")
    public ResponseEntity<List<GoalDto>> index(@PathVariable String slug) {

        Course course = courseService.findBySlug(slug);
        
        List<GoalDto> goalDtos = course.getGoals().stream()
                .sorted(Comparator.comparing(Goal::getId))
                .map(goal -> new GoalDto(goal.getId(), goal.getName()))
                .toList();

        return ResponseEntity.ok(goalDtos);
    }

    @PostMapping("/{slug}/goals")
    public ResponseEntity<?> store(
        @PathVariable String slug,
        @Valid @RequestBody GoalDto goalDto) {

        Course course = courseService.findBySlug(slug);
        Goal goal = goalService.createGoal(course, goalDto);

        GoalDto createdGoal = new GoalDto(goal.getId(), goal.getName());

        return new ResponseEntity<>(createdGoal, HttpStatus.CREATED);
    }

    @PutMapping("/{slug}/goals")
    public ResponseEntity<?> update(@PathVariable String slug,
            @RequestBody List<GoalDto> goals) {
 
        Course course = courseService.findBySlug(slug);

        goalService.updateGoals(course, goals);

        return ResponseEntity.ok(Map.of("message", "Metas actualizadas correctamente."));

    }

    @DeleteMapping("/goals/{goalId}")
    public ResponseEntity<?> delete(@PathVariable Long goalId) {
        
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();

    }
}