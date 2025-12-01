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

    Optional<Course> findBySlug(String slug);
    Optional<Course> findBySlugAndInstructorEmail(String slug, String email);
    List<Course> findByInstructorEmail(String email);
    Page<Course> findByInstructorEmail(String email, Pageable pageable);
    List<Course> findByNameContainingIgnoreCase(String name);
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByStatusOrderByCreatedAtDesc(CourseStatus status, Pageable pageable);
    List<Course> findByInstructorId(Long instructorId);
    List<Course> findByCategoryId(Long categoryId);
    List<Course> findByLevelId(Long levelId);
    List<Course> findByPriceId(Long priceId);
    boolean existsBySlug(String slug);

    // --- CONSULTA ACTUALIZADA PARA ENROLLMENT ---
    @Query("SELECT e.course FROM Enrollment e WHERE e.user.id = :userId")
    List<Course> findByStudentId(Long userId);
}