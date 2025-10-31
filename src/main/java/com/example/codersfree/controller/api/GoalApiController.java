package com.example.codersfree.controller.api;

import com.example.codersfree.dto.GoalDto;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Goal;
import com.example.codersfree.service.CourseService;
import com.example.codersfree.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class GoalApiController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private GoalService goalService;

    @GetMapping("/{slug}/goals")
    public List<Goal> index(@PathVariable String slug) {
        Course course = courseService.findBySlug(slug);
        return course.getGoals().stream()
                .sorted(Comparator.comparing(Goal::getId))
                .toList();
    }
    @PostMapping("/{slug}/goals")
    @ResponseStatus(HttpStatus.CREATED)
    public Goal store(
            @PathVariable String slug,
            @Valid @RequestBody GoalDto goalDto) {

        Course course = courseService.findBySlug(slug);
        return goalService.save(course, goalDto);
    }

    @PutMapping("/{slug}/goals")
    public List<Goal> update(
            @PathVariable String slug,
            @RequestBody List<Goal> goals) {

        Course course = courseService.findBySlug(slug);

        return goalService.updateGoals(course, goals);
    }

    @DeleteMapping("/goals/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }
}
