package com.example.codersfree.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 45, message = "El nombre no puede exceder los 45 caracteres")
    private String name;
}