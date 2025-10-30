package com.example.codersfree.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpdateDto {
    @NotBlank(message = "El nombre del curso no puede estar vacío.")
    @Size(min = 5, message = "El nombre debe tener al menos 5 caracteres.")
    private String name;

    @NotBlank(message = "El slug no puede estar vacío.")
    @Size(min = 5, message = "El slug debe tener al menos 5 caracteres.")
    private String slug;

    @Pattern(
        regexp = "^$|.{10,}", 
        message = "El resumen debe tener al menos 10 caracteres si se proporciona."
    )
    private String summary;

    @Pattern(
        regexp = "^$|.{20,}", 
        message = "El resumen debe tener al menos 10 caracteres si se proporciona."
    )
    private String description;

    @NotNull(message = "Debes seleccionar una categoría.")
    private Long categoryId;

    @NotNull(message = "Debes seleccionar un nivel.")
    private Long levelId;

    @NotNull(message = "Debes seleccionar un precio.")
    private Long priceId;
}