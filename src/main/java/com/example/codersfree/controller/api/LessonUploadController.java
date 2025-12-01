package com.example.codersfree.controller.api;

import com.example.codersfree.model.Lesson;
import com.example.codersfree.repository.LessonRepository;
import com.example.codersfree.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@RestController
@RequestMapping("/api/lessons") 
public class LessonUploadController {

    private static final Logger logger = LoggerFactory.getLogger(LessonUploadController.class);

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private FileStorageService fileStorageService;

    // Endpoint para subir un video
    @PostMapping("/{lessonId}/video")
    public ResponseEntity<Lesson> uploadVideo(@PathVariable Long lessonId, 
                                              @RequestParam("file") MultipartFile file) throws IOException {

        logger.info("INICIO: Petición de subida de video recibida para la lección ID: {}", lessonId);

        if (file == null || file.isEmpty()) {
            logger.error("ERROR 400: El archivo MultipartFile es nulo o vacío.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo 'file' es requerido y no puede estar vacío.");
        }
        
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lección no encontrada con ID: " + lessonId));

        try {
            String oldVideoPath = lesson.getVideoPath();
            String newVideoPath = fileStorageService.save("videos", file);
            lesson.setVideoPath(newVideoPath);
            lesson.setDuration(300); 
            
            // Eliminar el archivo anterior
            if (oldVideoPath != null && !oldVideoPath.isBlank()) {
                fileStorageService.delete(oldVideoPath);
            }
            
            Lesson savedLesson = lessonRepository.save(lesson);
            
            logger.info("FIN: Subida exitosa y lección {} actualizada.", lessonId);
            return ResponseEntity.ok(savedLesson);

        } catch (IOException e) {
            logger.error("ERROR 500: Fallo al procesar/guardar el archivo. Causa: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el archivo en el servidor.");
        }
    }
    
    // Endpoint para eliminar el video
    @DeleteMapping("/{lessonId}/video")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long lessonId) {
        logger.info("INICIO: Petición de eliminación de video para la lección ID: {}", lessonId);
        
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lección no encontrada con ID: " + lessonId));

        String videoPath = lesson.getVideoPath();
        
        if (videoPath != null && !videoPath.isBlank()) {
            try {
                fileStorageService.delete(videoPath);
            } catch (IOException e) {
                logger.error("ERROR: No se pudo eliminar el archivo físico {}. Causa: {}", videoPath, e.getMessage());
            }
        }

        // Limpiar el campo en la base de datos
        lesson.setVideoPath(null);
        lesson.setDuration(0);
        lesson.setIsPreview(false); // Limpiar preview también
        lessonRepository.save(lesson);
        
        logger.info("FIN: Ruta de video limpiada en DB para lección {}.", lessonId);
        return ResponseEntity.noContent().build();
    }
}