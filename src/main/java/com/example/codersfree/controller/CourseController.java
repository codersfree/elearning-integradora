package com.example.codersfree.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/courses")
public class CourseController {
    
    @GetMapping
    public String index()
    {
        return "courses/index";
    }

    @GetMapping("/detalle")
    public String show()
    {
        return "courses/show";
    }

}