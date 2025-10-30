package com.example.codersfree.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalDto {
    private Long id;

    @NotBlank(message = "El nombre de la meta no puede estar vacío.")
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres.")
    private String name;
}