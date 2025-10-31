package com.example.codersfree.repository;

import com.example.codersfree.model.Course;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Procedure(procedureName = "FindByInstructorEmail")
    List<Course> findByInstructorEmail(@Param("instructor_email") String email);

    //Realizar busquedas por el slug del curso
    Optional<Course> findBySlug(String slug);

    //Realizar busquedas por el nombre del curso
    List<Course> findByNameContainingIgnoreCase(String name);

    //Realizar busquedas por el id del instructor
    List<Course> findByInstructorId(Long instructorId);

    //Realizar busquedas por el id de la categoria
    List<Course> findByCategoryId(Long categoryId);

    //Realizar busqueda por el id del nivel
    List<Course> findByLevelId(Long levelId);

    //Realizar busqueda por el id del precio
    List<Course> findByPriceId(Long priceId);

    //Verificar si existe un curso por su slug
    boolean existsBySlug(String slug);

}