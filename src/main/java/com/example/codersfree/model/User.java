package com.example.codersfree.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45, nullable = false)
    private String name;

    @Column(length = 45, nullable = false, unique = true)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación de Roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_user", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "role_id") 
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // Relación de Cursos Comprados
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "course_user",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @Builder.Default
    private Set<Course> courses = new HashSet<>();

    // --- CORRECCIÓN AQUÍ: Lecciones Completadas ---
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "lesson_user",
        // joinColumns apunta a ESTA entidad (User)
        joinColumns = @JoinColumn(name = "user_id"),
        // inverseJoinColumns apunta a la OTRA entidad (Lesson)
        inverseJoinColumns = @JoinColumn(name = "lesson_id")
    )
    @Builder.Default
    private Set<Lesson> completedLessons = new HashSet<>();

    // Método helper para agregar curso
    public void addCourse(Course course) {
        this.courses.add(course);
    }

    // Método helper para verificar si completó una lección
    public boolean hasCompleted(Lesson lesson) {
        return this.completedLessons.stream()
                .anyMatch(l -> l.getId().equals(lesson.getId()));
    }
}