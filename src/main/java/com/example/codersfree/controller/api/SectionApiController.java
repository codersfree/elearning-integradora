package com.example.codersfree.controller.api;

import com.example.codersfree.model.Course;
import com.example.codersfree.model.Module;
import com.example.codersfree.repository.CourseRepository;
import com.example.codersfree.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class SectionApiController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    // 1. Obtener todas las secciones de un curso
    @GetMapping("/{slug}/sections")
    public ResponseEntity<List<Module>> getSections(@PathVariable String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Ordenamos por sortOrder si existe, o por ID
        List<Module> modules = course.getModules().stream()
                .sorted(Comparator.comparing(Module::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(modules);
    }

    // 2. Crear una nueva sección
    @PostMapping("/{slug}/sections")
    public ResponseEntity<Module> createSection(@PathVariable String slug, @RequestBody ModuleDto request) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        Module newModule = Module.builder()
                .name(request.getName())
                .sortOrder(request.getSortOrder())
                .course(course) // Asignamos la relación
                .build();

        Module savedModule = moduleRepository.save(newModule);
        return ResponseEntity.ok(savedModule);
    }

    // 3. Actualizar una sección
    @PutMapping("/{slug}/sections/{moduleId}")
    public ResponseEntity<Module> updateSection(@PathVariable String slug, @PathVariable Long moduleId, @RequestBody ModuleDto request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));
        
        // Validación de seguridad simple: asegurar que el módulo pertenece al curso del slug
        if (!module.getCourse().getSlug().equals(slug)) {
             return ResponseEntity.badRequest().build();
        }

        module.setName(request.getName());
        // module.setSortOrder(...) si decides implementar reordenamiento luego

        Module updatedModule = moduleRepository.save(module);
        return ResponseEntity.ok(updatedModule);
    }

    // 4. Eliminar una sección
    @DeleteMapping("/{slug}/sections/{moduleId}")
    public ResponseEntity<Void> deleteSection(@PathVariable String slug, @PathVariable Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));

        if (!module.getCourse().getSlug().equals(slug)) {
            return ResponseEntity.badRequest().build();
        }

        moduleRepository.delete(module);
        return ResponseEntity.noContent().build();
    }

    // DTO simple para recibir los datos del frontend
    @lombok.Data
    static class ModuleDto {
        private String name;
        private Integer sortOrder;
    }
}