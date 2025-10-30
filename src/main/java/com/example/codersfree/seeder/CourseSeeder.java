package com.example.codersfree.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.codersfree.enums.CourseStatus;
import com.example.codersfree.model.Category;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Level;
import com.example.codersfree.model.Price;
import com.example.codersfree.model.User;
import com.example.codersfree.repository.CategoryRepository;
import com.example.codersfree.repository.CourseRepository;
import com.example.codersfree.repository.LevelRepository;
import com.example.codersfree.repository.PriceRepository;
import com.example.codersfree.repository.UserRepository;

@Component
public class CourseSeeder implements Seeder {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Override
    public void seed() {
        System.out.println("Sembrando cursos...");
        
        User user = userRepository.findById(1L).orElseThrow();
        Category category = categoryRepository.findById(1L).orElseThrow();
        Level level = levelRepository.findById(1L).orElseThrow();
        Price price = priceRepository.findById(1L).orElseThrow();

        Course course = Course.builder()
                .name("curso prueba")
                .slug("curso-prueba")
                .status(CourseStatus.DRAFT)
                .summary("Este es un curso de prueba.")
                .description("Descripción detallada del curso de prueba.")
                .instructor(user)
                .category(category)
                .level(level)
                .price(price)
                .build();

        courseRepository.save(course);
    }
}
