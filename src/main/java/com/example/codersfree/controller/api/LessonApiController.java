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
                .isPreview(false)
                .module(module)
                .build();

        Lesson savedLesson = lessonRepository.save(newLesson);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLesson);
    }
    
    // 2. Actualizar Lección
    @PutMapping("/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long moduleId, @PathVariable Long lessonId, @RequestBody LessonDto request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lección no encontrada"));
        
        if (!lesson.getModule().getId().equals(moduleId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        lesson.setName(request.getName());
        lesson.setDescription(request.getDescription());
        // Se asegura que si no viene el campo isPreview, no se actualice a null
        if (request.getIsPreview() != null) {
             lesson.setIsPreview(request.getIsPreview());
        }

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
        
        // **IMPORTANTE:** Aquí se debería llamar al FileStorageService para eliminar el video físico antes de borrar la entidad.
        // Asumo que el borrado físico se manejará en el servicio o se agregará aquí.
        
        lessonRepository.delete(lesson);
        return ResponseEntity.noContent().build();
    }
}