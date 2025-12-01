package com.example.codersfree.service;

import com.example.codersfree.model.Course;
import com.example.codersfree.repository.CourseRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope
public class CartService {

    @Autowired
    private CourseRepository courseRepository;

    private Map<Long, Course> items = new HashMap<>();

    @Transactional 
    public void addItem(Long courseId) {
        if (!items.containsKey(courseId)) {
            Course course = courseRepository.findById(courseId).orElse(null);
            if (course != null) {
                // --- SOLUCIÓN AQUÍ ---
                // Forzamos a Hibernate a traer los datos AHORA, antes de guardar en sesión
                Hibernate.initialize(course.getPrice());
                Hibernate.initialize(course.getInstructor());
                Hibernate.initialize(course.getCategory()); 
                Hibernate.initialize(course.getLevel());
                // ---------------------
                
                items.put(courseId, course);
            }
        }
    }

    public void removeItem(Long courseId) {
        items.remove(courseId);
    }

    public Map<Long, Course> getItems() {
        return items;
    }
    
    public int getCount() {
        return items.size();
    }

    public BigDecimal getTotal() {
        return items.values().stream()
                // Aquí daba el error porque getPrice() era un proxy vacío
                .map(course -> course.getPrice().getValue()) 
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void clear() {
        items.clear();
    }

    public boolean contains(Long courseId) {
        return items.containsKey(courseId);
    }
}