package com.example.codersfree.repository;

import com.example.codersfree.enums.CourseStatus;
import com.example.codersfree.model.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    //Realizar busquedas por el slug del curso
    Optional<Course> findBySlug(String slug);

    //Realizar busquedas por el slug del curso y el email del instructor
    Optional<Course> findBySlugAndInstructorEmail(String slug, String email);

    //Realizar busquedas por el email del instructor
    List<Course> findByInstructorEmail(String email);

    //Realizar busquedas por el email del instructor con paginacion
    Page<Course> findByInstructorEmail(String email, Pageable pageable);

    //Realizar busquedas por el nombre del curso
    List<Course> findByNameContainingIgnoreCase(String name);

    //Realizar busquedas por el estado del curso
    List<Course> findByStatus(CourseStatus status);

    //Realizar busquedas por el estado del curso y ordenar por fecha de creacion descendente, limitando a 20 resultados
    List<Course> findByStatusOrderByCreatedAtDesc(CourseStatus status, Pageable pageable);

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

    @Query("SELECT c FROM User u JOIN u.courses c WHERE u.id = :userId")
    List<Course> findByStudentId(Long userId);

}