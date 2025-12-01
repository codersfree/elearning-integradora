package com.example.codersfree.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDto {
    
    private String name;
    private String description; // ⬅️ AÑADIR ESTE CAMPO
    private Boolean isPreview;
    
    private Integer position;
    private Integer duration;
}