package com.example.codersfree.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    @NotBlank(message = "El nombre no puede estar vacío.")
    private String name;

    @Email(message = "El correo electrónico no tiene un formato válido.")
    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password;
}