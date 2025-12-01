package com.example.codersfree.controller.api;

import com.example.codersfree.model.Course;
import com.example.codersfree.model.Module;
import com.example.codersfree.repository.CourseRepository;
import com.example.codersfree.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class ModuleApiController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    // 1. Obtener todas las secciones (GET)
    @GetMapping("/{slug}/sections")
    public ResponseEntity<List<Module>> getSections(@PathVariable String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Ordenamos por sortOrder
        List<Module> modules = course.getModules().stream()
                .sorted(Comparator.comparing(Module::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        // Nota: Se asume que aquí se realiza la inicialización de Lessons para evitar LazyInitializationException
        
        return ResponseEntity.ok(modules);
    }

    // 2. Crear una nueva sección (POST)
    @PostMapping("/{slug}/sections")
    public ResponseEntity<Module> createSection(@PathVariable String slug, @RequestBody ModuleDto request) {
        // ... (lógica de creación omitida) ...
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        Module newModule = Module.builder()
                .name(request.getName())
                .sortOrder(request.getSortOrder())
                .course(course)
                .build();

        Module savedModule = moduleRepository.save(newModule);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedModule);
    }

    // 3. Actualizar una sección (PUT)
    @PutMapping("/{slug}/sections/{moduleId}")
    public ResponseEntity<Module> updateSection(@PathVariable String slug, @PathVariable Long moduleId, @RequestBody ModuleDto request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Módulo no encontrado"));
        
        if (!module.getCourse().getSlug().equals(slug)) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // ⬅️ Actualización del nombre de la sección
        module.setName(request.getName());
        // Aquí iría module.setSortOrder(request.getSortOrder()) si se implementa reordenamiento.

        Module updatedModule = moduleRepository.save(module);
        return ResponseEntity.ok(updatedModule);
    }

    // 4. Eliminar una sección (DELETE)
    @DeleteMapping("/{slug}/sections/{moduleId}")
    public ResponseEntity<Void> deleteSection(@PathVariable String slug, @PathVariable Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Módulo no encontrado"));

        if (!module.getCourse().getSlug().equals(slug)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // NOTA: CascadeType.ALL en la relación modules.lessons asegura que las lecciones se eliminen.
        // Si hay recursos físicos asociados a las lecciones, la lógica de eliminación física debe estar aquí.

        moduleRepository.delete(module);
        return ResponseEntity.noContent().build();
    }

    // DTO simple para recibir los datos del frontend (Usado para POST y PUT)
    @lombok.Data
    static class ModuleDto {
        private String name;
        private Integer sortOrder;
    }
}