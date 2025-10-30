package com.example.codersfree.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.codersfree.enums.CourseStatus;
import com.example.codersfree.model.Category;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Level;
import com.example.codersfree.model.Price;
import com.example.codersfree.model.User;
import com.example.codersfree.repository.CourseRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class CourseSeeder implements Seeder {

    @Autowired
    private CourseRepository courseRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void seed() {
        System.out.println("Sembrando cursos...");
        
        User user = entityManager.getReference(User.class, 1L);
        Category category = entityManager.getReference(Category.class, 1L);
        Level level = entityManager.getReference(Level.class, 1L);
        Price price = entityManager.getReference(Price.class, 1L);

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
