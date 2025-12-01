package com.example.codersfree.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.example.codersfree.enums.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
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

    // --- RELACIONES MANY-TO-ONE ---
    
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

    // --- RELACIONES ONE-TO-MANY ---

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC") 
    private Set<Module> modules = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Requirement> requirements = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Goal> goals = new HashSet<>();

    // --- NUEVA RELACIÓN: MATRÍCULAS (ENROLLMENTS) ---
    // Nos permite acceder al historial de ventas de este curso
    @Builder.Default
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments = new HashSet<>();


    // --- MÉTODOS CALCULADOS / LOGICA DE NEGOCIO ---

    @Transient // No es una columna en BD
    public String getImage() {
        if (imagePath == null || imagePath.isBlank()) {
            return "https://placehold.co/750x422/eeeeee/333333?text=Sin+Imagen";
        }
        return "/uploads/" + imagePath;
    }

    // 1. Ganancias Totales Históricas
    @Transient
    public BigDecimal getTotalEarnings() {
        if (enrollments == null || enrollments.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return enrollments.stream()
                .map(Enrollment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 2. Ganancias Este Mes
    @Transient
    public BigDecimal getEarningsThisMonth() {
        if (enrollments == null || enrollments.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        YearMonth currentMonth = YearMonth.now();
        
        return enrollments.stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(currentMonth))
                .map(Enrollment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 3. Cantidad de Inscritos Este Mes
    @Transient
    public long getEnrollmentsCountThisMonth() {
        if (enrollments == null || enrollments.isEmpty()) {
            return 0;
        }
        
        YearMonth currentMonth = YearMonth.now();
        
        return enrollments.stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(currentMonth))
                .count();
    }
}