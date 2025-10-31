package com.example.codersfree.service;

import com.example.codersfree.dto.GoalDto;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Goal;
import com.example.codersfree.repository.GoalRepository;

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
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Transactional(readOnly = true)
    public Goal findById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta no encontrada con ID: " + id));
    }

    @Transactional
    public Goal save(Course course, GoalDto dto) {

        Goal goal = Goal.builder()
                .name(dto.getName())
                .course(course)
                .build();

        return goalRepository.save(goal);

    }

    @Transactional
    public List<Goal> updateGoals(Course course, List<Goal> goalsInput) {

        Map<Long, Goal> existingGoals = goalRepository.findAllById(
                goalsInput.stream().map(Goal::getId).toList()).stream()
                .collect(Collectors.toMap(Goal::getId, Function.identity()));

        goalsInput.forEach(inputGoal -> {
            Goal managedGoal = existingGoals.get(inputGoal.getId());
            if (managedGoal != null) {
                String newName = inputGoal.getName();
                if (newName != null && !newName.isBlank() && !newName.equals(managedGoal.getName())) {
                    managedGoal.setName(newName);
                }
            }
        });
        
        return goalRepository.saveAll(existingGoals.values());

    }

    @Transactional
    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new EntityNotFoundException("Meta no encontrada con ID: " + goalId);
        }
        goalRepository.deleteById(goalId);
    }
}