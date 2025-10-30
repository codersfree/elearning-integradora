package com.example.codersfree.service;

import com.example.codersfree.dto.GoalDto;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Goal;
import com.example.codersfree.repository.GoalRepository;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GoalService {

    private static final Logger log = LoggerFactory.getLogger(GoalService.class);

    @Autowired
    private GoalRepository goalRepository;

    @Transactional(readOnly = true)
    public Goal findById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meta no encontrada con ID: " + id));
    }

    @Transactional
    public Goal createGoal(Course course, GoalDto dto) {

        Goal goal = Goal.builder()
                .name(dto.getName())
                .course(course)
                .build();

        return goalRepository.save(goal);

    }

    @Transactional
    public void updateGoals(Course course, List<GoalDto> goalDtos) {

        Map<Long, Goal> goals = goalRepository.findAllById(
                goalDtos.stream()
                        .map(GoalDto::getId)
                        .toList())
                .stream()
                .collect(Collectors.toMap(Goal::getId, Function.identity()));

        for (GoalDto dto : goalDtos) {

            String newName = dto.getName();

            if (newName == null || newName.isBlank()) {
                log.warn("Ignorando meta con nombre vacío para curso ID: {}", course.getId());
                continue;
            }

            Goal goal = goals.get(dto.getId());

            if (!goal.getName().equals(newName)) {
                goal.setName(newName);
                log.debug("Actualizando meta ID {}: {}", dto.getId(), newName);
            }
        }

        goalRepository.saveAll(goals.values());

    }

    
    @Transactional
    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new EntityNotFoundException("Meta no encontrada con ID: " + goalId);
        }
        goalRepository.deleteById(goalId);
        log.info("Meta eliminada con ID: {}", goalId);
    }
}