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

    //Carga ansiosa de roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_user", // Nombre de la tabla intermedia
        joinColumns = @JoinColumn(name = "user_id"), // Clave foránea de esta entidad (User)
        inverseJoinColumns = @JoinColumn(name = "role_id") // Clave foránea de la otra entidad (Role)
    )
    private Set<Role> roles = new HashSet<>();
}