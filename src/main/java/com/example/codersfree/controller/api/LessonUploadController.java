package com.example.codersfree.controller.api;

import com.example.codersfree.model.Lesson;
import com.example.codersfree.repository.LessonRepository;
import com.example.codersfree.service.FileStorageService;
import com.example.codersfree.service.VideoDurationService; 
import com.example.codersfree.service.ThumbnailService; 
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
    
    @Autowired
    private VideoDurationService durationService; 

    @Autowired
    private ThumbnailService thumbnailService; 

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
            String oldThumbnailPath = lesson.getImagePath();

            // 1. Guardar el nuevo video
            String newVideoPath = fileStorageService.save("videos", file);

            // 2. Extraer Duración y Generar Miniatura
            Integer realDuration = durationService.getDurationInSeconds(file); 
            String newThumbnailPath = thumbnailService.generateThumbnail(newVideoPath, "thumbnails");
            
            // 3. Limpiar archivos antiguos
            if (oldVideoPath != null && !oldVideoPath.isBlank()) {
                fileStorageService.delete(oldVideoPath);
            }
            if (oldThumbnailPath != null && !oldThumbnailPath.isBlank()) {
                 fileStorageService.delete(oldThumbnailPath);
            }

            // 4. Actualizar la lección
            lesson.setVideoPath(newVideoPath);
            lesson.setDuration(realDuration); 
            lesson.setImagePath(newThumbnailPath); 
            
            Lesson savedLesson = lessonRepository.save(lesson);
            
            logger.info("FIN: Subida y miniatura exitosa para lección {}.", lessonId);
            return ResponseEntity.ok(savedLesson);

        } catch (IOException e) {
            logger.error("ERROR 500: Fallo al procesar/guardar/generar thumbnail. Causa: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el archivo o generar la miniatura.");
        }
    }
    
    // Endpoint para eliminar el video
    @DeleteMapping("/{lessonId}/video")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long lessonId) {
        logger.info("INICIO: Petición de eliminación de video para la lección ID: {}", lessonId);
        
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lección no encontrada con ID: " + lessonId));

        String videoPath = lesson.getVideoPath();
        String thumbnailPath = lesson.getImagePath();
        
        // 1. ELIMINAR VIDEO FÍSICO
        if (videoPath != null && !videoPath.isBlank()) {
            try {
                fileStorageService.delete(videoPath);
            } catch (IOException e) {
                logger.error("ERROR: No se pudo eliminar el video físico {}.", videoPath);
            }
        }
        
        // 2. ELIMINAR MINIATURA FÍSICA
        if (thumbnailPath != null && !thumbnailPath.isBlank()) {
            try {
                fileStorageService.delete(thumbnailPath);
            } catch (IOException e) {
                 logger.error("ERROR: No se pudo eliminar la miniatura física {}.", thumbnailPath);
            }
        }

        // 3. Limpiar campos en la base de datos
        lesson.setVideoPath(null);
        lesson.setImagePath(null); 
        lesson.setDuration(0);
        // CORRECCIÓN CLAVE: Eliminada la referencia a setIsPreview, ya que el campo fue eliminado.
        // Si el campo fue renombrado a isPublish, debería ser: lesson.setIsPublish(false);
        // Asumo que quieres que se elimine cualquier referencia a esos campos booleanos.
        
        lessonRepository.save(lesson);
        
        logger.info("FIN: Video y miniatura limpiados en DB para lección {}.", lessonId);
        return ResponseEntity.noContent().build();
    }
}