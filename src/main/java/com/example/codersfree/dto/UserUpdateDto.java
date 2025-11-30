package com.example.codersfree.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {
    @NotBlank(message = "El nombre no puede estar vacío.")
    private String name;

    @Email(message = "El correo electrónico no tiene un formato válido.")
    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    private String email;

    // Se usa Pattern para permitir cadena vacía o que cumpla el tamaño mínimo.
    @Pattern(
        regexp = "^$|.{6,255}",
        message = "La contraseña debe estar vacía o tener al menos 6 caracteres."
    )
    private String password; 
    
    // Campo para recibir los IDs de los roles seleccionados
    private Set<Long> roles = new HashSet<>();
}