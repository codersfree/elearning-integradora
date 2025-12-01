package com.example.codersfree.service;

import com.example.codersfree.dto.CourseCreateDto;
import com.example.codersfree.dto.CourseUpdateDto;
import com.example.codersfree.dto.MessageDto;
import com.example.codersfree.enums.CourseStatus;
import com.example.codersfree.model.*;
import com.example.codersfree.model.Module;
import com.example.codersfree.repository.CourseRepository;
import com.example.codersfree.repository.EnrollmentRepository; // Importante
import com.example.codersfree.repository.specification.CourseSpecification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;
    
    // --- INYECCIÓN NUEVA ---
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private FileStorageService storage;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Course> findByInstructorEmail(String email) {
        return courseRepository.findByInstructorEmail(email);
    }

    @Transactional(readOnly = true)
    public Page<Course> findByInstructorEmailPaginate(String email, Pageable pageable) {
        Page<Course> courses = courseRepository.findByInstructorEmail(email, pageable);
        return courses;
    }

    @Transactional(readOnly = true)
    public Course findBySlug(String slug) {
        return courseRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));
    }

    @Transactional(readOnly = true)
    public Course findBySlugAndInstructorEmail(String slug, String email) {
        return courseRepository.findBySlugAndInstructorEmail(slug, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public List<Course> getPublishedCourses() {
        return courseRepository.findByStatus(CourseStatus.PUBLISHED);
    }

    public List<Course> getLatestPublishedCourses() {
        Pageable limit = PageRequest.of(0, 20);
        return courseRepository.findByStatusOrderByCreatedAtDesc(CourseStatus.PUBLISHED, limit);
    }

    // --- MÉTODO ACTUALIZADO: Usar EnrollmentRepository ---
    @Transactional(readOnly = true)
    public List<Course> findEnrolledCourses(Long userId) {
        // Obtenemos los cursos a través de las matrículas
        return enrollmentRepository.findByUserId(userId).stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toList());
    }

    public Page<Course> searchAndFilterCourses(
            String searchTerm, List<Long> categoryIds, List<Long> levelIds, List<Long> priceIds, Pageable pageable) {
        Specification<Course> spec = CourseSpecification.hasStatus(CourseStatus.PUBLISHED);
        if (searchTerm != null && !searchTerm.isBlank()) spec = spec.and(CourseSpecification.nameContains(searchTerm));
        if (categoryIds != null && !categoryIds.isEmpty()) spec = spec.and(CourseSpecification.inCategories(categoryIds));
        if (levelIds != null && !levelIds.isEmpty()) spec = spec.and(CourseSpecification.inLevels(levelIds));
        if (priceIds != null && !priceIds.isEmpty()) spec = spec.and(CourseSpecification.inPrices(priceIds));
        Page<Course> courses = courseRepository.findAll(spec, pageable);
        return courses;
    }

    @Transactional
    public Course createCourse(CourseCreateDto dto, String instructorEmail) {
        if (courseRepository.existsBySlug(dto.getSlug())) throw new IllegalArgumentException("El slug ya está en uso.");
        Category category = entityManager.getReference(Category.class, dto.getCategoryId());
        Level level = entityManager.getReference(Level.class, dto.getLevelId());
        Price price = entityManager.getReference(Price.class, dto.getPriceId());
        User instructor = userService.findByEmail(instructorEmail);
        Course course = Course.builder().name(dto.getName()).slug(dto.getSlug()).category(category).level(level).price(price).instructor(instructor).status(CourseStatus.PUBLISHED).build();
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(String slug, CourseUpdateDto dto, MultipartFile file) throws IOException {
        if (!slug.equals(dto.getSlug()) && courseRepository.existsBySlug(dto.getSlug())) throw new IllegalArgumentException("Slug en uso.");
        Course course = findBySlug(slug);
        if (file != null && !file.isEmpty()) {
            if (course.getImagePath() != null && !course.getImagePath().isBlank()) storage.delete(course.getImagePath());
            String imagePath = storage.save("courses/", file);
            course.setImagePath(imagePath);
        }
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
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Se requiere video.");
        Course course = findBySlug(slug);
        if (course.getVideoPath() != null && !course.getVideoPath().isBlank()) storage.delete(course.getVideoPath());
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

    @Transactional(readOnly = true)
    public Lesson findFirstIncompleteLesson(Course course, User user) {
        List<Module> sortedModules = course.getModules().stream().sorted(Comparator.comparing(Module::getId)).collect(Collectors.toList());
        for (Module module : sortedModules) {
            List<Lesson> sortedLessons = module.getLessons().stream().sorted(Comparator.comparing(Lesson::getPosition)).collect(Collectors.toList());
            for (Lesson lesson : sortedLessons) {
                if (!user.hasCompleted(lesson)) return lesson;
            }
        }
        if (!sortedModules.isEmpty()) {
            List<Lesson> firstModuleLessons = sortedModules.get(0).getLessons().stream().sorted(Comparator.comparing(Lesson::getPosition)).collect(Collectors.toList());
            if (!firstModuleLessons.isEmpty()) return firstModuleLessons.get(0);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public int calculateProgress(Course course, User user) {
        long totalLessons = course.getModules().stream().mapToLong(m -> m.getLessons().size()).sum();
        if (totalLessons == 0) return 0;
        long completedCount = user.getCompletedLessons().stream().filter(l -> l.getModule().getCourse().getId().equals(course.getId())).count();
        return (int) ((completedCount * 100) / totalLessons);
    }

    @Transactional
    public void toggleLessonCompletion(User user, Lesson lesson) {
        if (user.hasCompleted(lesson)) user.getCompletedLessons().removeIf(l -> l.getId().equals(lesson.getId()));
        else user.getCompletedLessons().add(lesson);
    }
}