package com.example.codersfree.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.example.codersfree.enums.CourseStatus;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, unique = true, length = 45)
    private String slug;

    @Column(length = 255)
    private String summary;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "video_path")
    private String videoPath;

    @Lob
    @Column(name = "welcome_message", columnDefinition = "TEXT")
    private String welcomeMessage;

    @Lob
    @Column(name = "goodbye_message", columnDefinition = "TEXT")
    private String goodbyeMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Relaciones ManyToOne ---
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", referencedColumnName = "id", nullable = false)
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_id", referencedColumnName = "id", nullable = false)
    private Price price;

    // --- Relaciones OneToMany CORREGIDAS ---
    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Module> modules = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Requirement> requirements = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Goal> goals = new HashSet<>();

    // @OneToMany(mappedBy = "course")
    // private Set<Enrolled> enrollments = new HashSet<>();

    @Transient
    public String getImage() {
        if (imagePath == null || imagePath.isBlank()) {
            return "https://placehold.co/750x422/eeeeee/333333?text=Sin+Imagen";
        }
        return "/uploads/" + imagePath;
    }
}