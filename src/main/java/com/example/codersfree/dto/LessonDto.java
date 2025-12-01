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
    private String description;
    
    // ELIMINADO: private Boolean isPublish;
    
    private Integer position;
    private Integer duration;
}