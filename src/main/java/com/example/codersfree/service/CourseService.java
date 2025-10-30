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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    // Repositorios
    @Autowired
    private CourseRepository courseRepository;

    // Servicios
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileStorageService storage;

    @Autowired
    private LevelService levelService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Course> findByInstructorEmail(String email) {
        User user = userService.findByEmail(email);
        return courseRepository.findByInstructorId(user.getId());
    }

    @Transactional(readOnly = true)
    public Course findBySlug(String slug) {
        return courseRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado con slug: " + slug));
    }

    //Transacciones

    @Transactional
    public Course createCourse(CourseCreateDto dto, String instructorEmail) {

        //Validar slug
        if (courseRepository.existsBySlug(dto.getSlug())) {
            throw new IllegalArgumentException("El slug ya está en uso. Por favor, elige otro.");
        }

        Category category = categoryService.findById(dto.getCategoryId());
        Level level = levelService.findById(dto.getLevelId());
        Price price = priceService.findById(dto.getPriceId());
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
    public Course updateCourse(String slug, CourseUpdateDto dto, MultipartFile file) {

        // Validar slug único si ha cambiado
        if (courseRepository.existsBySlug(dto.getSlug()) && !slug.equals(dto.getSlug())) {
            throw new IllegalArgumentException("El slug '" + dto.getSlug() + "' ya está en uso por otro curso.");
        }

        // Buscar entidades
        Course course = findBySlug(slug);
        Category category = categoryService.findById(dto.getCategoryId());
        Level level = levelService.findById(dto.getLevelId());
        Price price = priceService.findById(dto.getPriceId());

        // Procesar la imagen (si se subió una nueva)
        if (file != null && !file.isEmpty()) {

            // Borrar imagen anterior si existe
            if (course.getImagePath() != null) {
                storage.delete(course.getImagePath());
            }

            try {
                String imagePath = storage.save("courses/", file);
                course.setImagePath(imagePath);
            } catch (IOException e) {
                throw new IllegalArgumentException("Error al procesar la subida de la imagen: " + e.getMessage());
            }

        }

        // Actualizar la entidad
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
    public Course updateCourseVideo(String existingSlug, MultipartFile file) {
        
        // 1. Validar que el archivo no esté vacío
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Se requiere un archivo de video.");
        }

        // 2. Buscar el curso (lanza EntityNotFoundException si no existe)
        Course courseDB = findBySlug(existingSlug);

        // 3. Definir el directorio de videos
        String videoDirectory = "courses/videos/";

        try {
            // 4. Borrar el video anterior si existe
            if (courseDB.getVideoPath() != null && !courseDB.getVideoPath().isBlank()) {
                storage.delete(courseDB.getVideoPath());
            }

            // 5. Guardar el nuevo video
            String videoPath = storage.save(videoDirectory, file);
            
            // 6. Actualizar la entidad
            courseDB.setVideoPath(videoPath);
            
            // 7. Guardar y devolver
            return courseRepository.save(courseDB);

        } catch (IOException e) {
            log.error("Error de E/S al guardar el video: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error al procesar la subida del video: " + e.getMessage());
        }
    }

    @Transactional
    public Course updateCourseMessage(String existingSlug, MessageDto dto) {
        
        Course courseDB = findBySlug(existingSlug);

        courseDB.setWelcomeMessage(dto.getWelcomeMessage());
        courseDB.setGoodbyeMessage(dto.getGoodbyeMessage());

        return courseRepository.save(courseDB);
    }

}