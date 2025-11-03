package com.example.codersfree.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.codersfree.model.Cover;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoverDto {

    public CoverDto(Cover cover)
    {
        this.title = cover.getTitle();
        this.startAt = cover.getStartAt();
        this.endAt = cover.getEndAt();
        this.active = cover.isActive();
    }

    @NotBlank(message = "El título no puede estar vacío")
    private String title;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endAt;

    private boolean active = true;

}