package com.example.codersfree.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.codersfree.model.Cover;
import com.example.codersfree.service.CourseService;
import com.example.codersfree.service.CoverService;

@Controller
public class WelcomeController {

    @Autowired
    private CoverService coverService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/")
    public String index(Model model)
    {
        model.addAttribute("covers", coverService.getActiveCoversForToday());
        model.addAttribute("latestCourses", courseService.getLatestPublishedCourses());
        
        return "welcome";
    }

    /* @GetMapping("/")
    @ResponseBody
    public List<Cover> index()
    {
        return coverService.getActiveCoversForToday();
    } */

}