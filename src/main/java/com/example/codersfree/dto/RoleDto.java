package com.example.codersfree.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    @NotBlank(message = "El nombre no puede estar vacío.")
    private String name;
}
