package com.example.codersfree.model;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore; 

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "video_path", length = 255) 
    private String videoPath;

    // Usaremos este campo para la ruta de la miniatura
    @Column(name = "image_path", length = 255) 
    private String imagePath; 

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    private Integer duration;

    @Column(name = "is_preview", nullable = false)
    private Boolean isPreview;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Rompe el ciclo Lesson -> Module -> Lesson
    @JsonIgnore 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "id", nullable = false)
    private Module module;
}