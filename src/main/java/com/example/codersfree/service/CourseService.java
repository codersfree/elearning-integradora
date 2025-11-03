package com.example.codersfree.service;

import com.example.codersfree.dto.CourseCreateDto;
import com.example.codersfree.dto.CourseUpdateDto;
import com.example.codersfree.dto.MessageDto;
import com.example.codersfree.enums.CourseStatus;
import com.example.codersfree.model.Category;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Level;
import com.example.codersfree.model.Price;
import com.example.codersfree.model.User;
import com.example.codersfree.repository.CourseRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
public class CourseService {

    // Repositorios
    @Autowired
    private CourseRepository courseRepository;

    // Servicios
    @Autowired
    private FileStorageService storage;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<Course> findByInstructorEmail(String email) {
        return courseRepository.findByInstructorEmail(email);
    }

    @Transactional(readOnly = true)
    public Course findBySlug(String slug) {
        return courseRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado con slug: " + slug));
    }

    @Transactional(readOnly = true)
    public Course findBySlugAndInstructorEmail(String slug, String email) {
        return courseRepository.findBySlugAndInstructorEmail(slug, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado con slug: " + slug + " y email de instructor: " + email));
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    //Transacciones
    @Transactional
    public Course createCourse(CourseCreateDto dto, String instructorEmail) {

        //Validar slug
        if (courseRepository.existsBySlug(dto.getSlug())) {
            throw new IllegalArgumentException("El slug ya está en uso. Por favor, elige otro.");
        }

        Category category = entityManager.getReference(Category.class, dto.getCategoryId());
        Level level = entityManager.getReference(Level.class, dto.getLevelId());
        Price price = entityManager.getReference(Price.class, dto.getPriceId());
        User instructor = userService.findByEmail(instructorEmail);

        Course course = Course.builder()
                .name(dto.getName())
                .slug(dto.getSlug())
                .category(category)
                .level(level)
                .price(price)
                .instructor(instructor)
                .status(CourseStatus.DRAFT)
                .build();

        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(String slug, CourseUpdateDto dto, MultipartFile file) throws IOException {

        // Validar slug único si ha cambiado
        if (!slug.equals(dto.getSlug()) && courseRepository.existsBySlug(dto.getSlug())) {
            throw new IllegalArgumentException("El slug '" + dto.getSlug() + "' ya está en uso por otro curso.");
        }

        // Buscar curso
        Course course = findBySlug(slug);

        // Actualizar imagen si se proporciona un nuevo archivo
        if (file != null && !file.isEmpty()) {

            // Borrar imagen anterior si existe
            if (course.getImagePath() != null && !course.getImagePath().isBlank()) {
                storage.delete(course.getImagePath());
            }

            String imagePath = storage.save("courses/", file);
            course.setImagePath(imagePath);

        }

        // Actualizar la entidad
        Category category = entityManager.getReference(Category.class, dto.getCategoryId());
        Level level = entityManager.getReference(Level.class, dto.getLevelId());
        Price price = entityManager.getReference(Price.class, dto.getPriceId());

        course.setName(dto.getName());
        course.setSlug(dto.getSlug());
        course.setSummary(dto.getSummary());
        course.setDescription(dto.getDescription());
        course.setCategory(category);
        course.setLevel(level);
        course.setPrice(price);

        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourseVideo(String slug, MultipartFile file) throws IOException {
        
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Se requiere un archivo de video.");
        }

        Course course = findBySlug(slug);

        if (course.getVideoPath() != null && !course.getVideoPath().isBlank()) {
            storage.delete(course.getVideoPath());
        }

        String videoPath = storage.save("courses/videos/", file);
        
        course.setVideoPath(videoPath);
        
        return courseRepository.save(course);

    }

    @Transactional
    public Course updateCourseMessage(String slug, MessageDto dto) {
        
        Course course = findBySlug(slug);

        course.setWelcomeMessage(dto.getWelcomeMessage());
        course.setGoodbyeMessage(dto.getGoodbyeMessage());

        return courseRepository.save(course);
    }

}