package com.example.codersfree.repository;

import com.example.codersfree.model.Enrollment;
import com.example.codersfree.model.User;
import com.example.codersfree.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    // Verificar si un usuario ya tiene matrícula en un curso
    boolean existsByUserAndCourse(User user, Course course);

    // Obtener todas las matrículas de un usuario
    List<Enrollment> findByUserId(Long userId);
}