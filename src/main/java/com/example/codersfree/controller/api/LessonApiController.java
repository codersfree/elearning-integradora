package com.example.codersfree.controller.api;

import com.example.codersfree.dto.LessonDto;
import com.example.codersfree.model.Lesson;
import com.example.codersfree.model.Module;
import com.example.codersfree.repository.LessonRepository;
import com.example.codersfree.repository.ModuleRepository;
import com.example.codersfree.service.FileStorageService; // Importado para la eliminación de archivos
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/modules")
public class LessonApiController {

    private static final Logger logger = LoggerFactory.getLogger(LessonApiController.class);

    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private FileStorageService fileStorageService; // Inyección para eliminación de archivos

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
    
    // 2. Actualizar Lección (Maneja el guardado de la descripción)
    @PutMapping("/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long moduleId, @PathVariable Long lessonId, @RequestBody LessonDto request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lección no encontrada"));
        
        if (!lesson.getModule().getId().equals(moduleId)) {
            logger.warn("Intento de actualización de lección {} por módulo {} (No coincidente).", lessonId, moduleId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // --- Guardando la descripción (y otros campos editables) ---
        lesson.setName(request.getName());
        lesson.setDescription(request.getDescription()); // ⬅️ GUARDANDO LA DESCRIPCIÓN
        
        if (request.getIsPreview() != null) {
             lesson.setIsPreview(request.getIsPreview());
        }

        Lesson updatedLesson = lessonRepository.save(lesson);
        logger.info("Lección {} actualizada. Descripción persistida.", lessonId);
        return ResponseEntity.ok(updatedLesson);
    }
    
    // 3. Eliminar Lección (Incluye eliminación de archivos físicos)
    @DeleteMapping("/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long moduleId, @PathVariable Long lessonId) {
         Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lección no encontrada"));

        if (!lesson.getModule().getId().equals(moduleId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        String videoPath = lesson.getVideoPath();
        String imagePath = lesson.getImagePath(); // Ruta de la miniatura

        // 1. Eliminar archivos físicos (video e imagen)
        try {
            if (videoPath != null && !videoPath.isBlank()) {
                fileStorageService.delete(videoPath);
                logger.info("Video físico eliminado: {}", videoPath);
            }
            if (imagePath != null && !imagePath.isBlank()) {
                fileStorageService.delete(imagePath);
                logger.info("Miniatura física eliminada: {}", imagePath);
            }
        } catch (IOException e) {
            logger.error("Error al eliminar archivos de lección {}: {}", lessonId, e.getMessage());
            // No detenemos la transacción, solo registramos el error.
        }
        
        // 2. Eliminar la entidad de la base de datos
        lessonRepository.delete(lesson);
        logger.info("Lección {} eliminada de la base de datos.", lessonId);
        
        return ResponseEntity.noContent().build();
    }
}