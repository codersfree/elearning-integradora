package com.example.codersfree.controller.api;

import com.example.codersfree.dto.GoalDto;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Goal;
import com.example.codersfree.service.CourseService;
import com.example.codersfree.service.GoalService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instructor/courses")
public class GoalApiController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private GoalService goalService;

    @PostMapping("/{slug}/goals")
    public ResponseEntity<?> createGoal(
        @PathVariable String slug,
        @Valid @RequestBody GoalDto goalDto) {

        Course course = courseService.findBySlug(slug);
        Goal goal = goalService.createGoal(course, goalDto);

        GoalDto createdGoal = new GoalDto(goal.getId(), goal.getName());

        return new ResponseEntity<>(createdGoal, HttpStatus.CREATED);
    }

    @PutMapping("/{slug}/goals")
    public ResponseEntity<?> updateGoals(@PathVariable String slug,
            @RequestBody List<GoalDto> goals) {
 
        Course course = courseService.findBySlug(slug);

        goalService.updateGoals(course, goals);

        return ResponseEntity.ok(Map.of("message", "Metas actualizadas correctamente."));

    }

    @DeleteMapping("/goals/{goalId}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long goalId) {
        
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();

    }
}