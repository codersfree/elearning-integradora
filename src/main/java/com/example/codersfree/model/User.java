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

    // --- NUEVA RELACIÓN: Matrículas (Enrollments) ---
    // Sustituye a la antigua lista directa de cursos
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Enrollment> enrollments = new HashSet<>();

    // Relación de Lecciones Completadas (Progreso)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "lesson_user",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "lesson_id")
    )
    @Builder.Default
    private Set<Lesson> completedLessons = new HashSet<>();

    // --- HELPER ACTUALIZADO: Verifica si está inscrito a través de Enrollment ---
    public boolean isEnrolled(Long courseId) {
        if (this.enrollments == null) return false;
        return this.enrollments.stream()
                .anyMatch(e -> e.getCourse().getId().equals(courseId));
    }

    // Helper: Verifica si completó una lección
    public boolean hasCompleted(Lesson lesson) {
        if (this.completedLessons == null) return false;
        return this.completedLessons.stream()
                .anyMatch(l -> l.getId().equals(lesson.getId()));
    }
}