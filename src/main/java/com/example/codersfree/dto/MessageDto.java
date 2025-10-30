package com.example.codersfree.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    @Size(max = 2000, message = "El mensaje de bienvenida no debe exceder 2000 caracteres.")
    private String welcomeMessage;

    @Size(max = 2000, message = "El mensaje de felicitación no debe exceder 2000 caracteres.")
    private String goodbyeMessage;
}