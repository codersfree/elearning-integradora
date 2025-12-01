package com.example.codersfree.controller.api;

import com.example.codersfree.dto.LessonDto;
import com.example.codersfree.model.Lesson;
import com.example.codersfree.model.Module;
import com.example.codersfree.repository.LessonRepository;
import com.example.codersfree.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;

@RestController
@RequestMapping("/api/modules")
public class LessonApiController {
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private LessonRepository lessonRepository;

    // 1. Crear una nueva lección
    @PostMapping("/{moduleId}/lessons")
    public ResponseEntity<Lesson> createLesson(@PathVariable Long moduleId, @RequestBody LessonDto request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Módulo no encontrado"));

        Integer nextPosition = module.getLessons().stream()
                                    .map(Lesson::getPosition)
                                    .max(Comparator.naturalOrder())
                                    .orElse(0) + 1;

        Lesson newLesson = Lesson.builder()
                .name(request.getName())
                .position(nextPosition)
                .duration(0) 
                // isPublish/isPreview ELIMINADO del builder
                .module(module)
                .build();

        Lesson savedLesson = lessonRepository.save(newLesson);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLesson);
    }
    
    // 2. Actualizar Lección (Maneja Descripción)
    @PutMapping("/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long moduleId, @PathVariable Long lessonId, @RequestBody LessonDto request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lección no encontrada"));
        
        if (!lesson.getModule().getId().equals(moduleId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Actualización de campos
        if (request.getName() != null) {
            lesson.setName(request.getName());
        }
        if (request.getDescription() != null) {
            lesson.setDescription(request.getDescription());
        }
        
        // Lógica de isPublish/isPreview ELIMINADA.

        Lesson updatedLesson = lessonRepository.save(lesson);
        return ResponseEntity.ok(updatedLesson);
    }
    
    // 3. Eliminar Lección
    @DeleteMapping("/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long moduleId, @PathVariable Long lessonId) {
         Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lección no encontrada"));

        if (!lesson.getModule().getId().equals(moduleId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // NOTA: El controlador de subida (LessonUploadController) debe ser ajustado también.
        
        lessonRepository.delete(lesson);
        return ResponseEntity.noContent().build();
    }
}