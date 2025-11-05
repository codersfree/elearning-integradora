package com.example.codersfree.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.codersfree.service.CategoryService;
import com.example.codersfree.service.CourseService;
import com.example.codersfree.service.LevelService;
import com.example.codersfree.service.PriceService;

@Controller
@RequestMapping("/courses")
public class CourseController {
    
    @Autowired
    private CourseService courseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LevelService levelService;

    @Autowired
    private PriceService priceService;

    @GetMapping
    public String index(
        // --- Parámetros de los Filtros ---
        @RequestParam(value = "search", required = false) String searchTerm,
        @RequestParam(value = "categories", required = false) List<Long> categoryIds,
        @RequestParam(value = "levels", required = false) List<Long> levelIds,
        @RequestParam(value = "prices", required = false) List<Long> priceIds,
        
        // --- Parámetros de Paginación y Orden ---
        @RequestParam(value = "sort", required = false, defaultValue = "createdAt_desc") String sortParam,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "8") int size,

        // --- Modelo ---
        Model model
    )
    {
        //Determinar el orden
        Sort sort = switch (sortParam) {
            case "createdAt_asc" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "name_asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name_desc" -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.by(Sort.Direction.DESC, "createdAt"); 
        };

        Pageable pageable = PageRequest.of(page, size, sort);

        // Cargar el modelo course
        model.addAttribute("courses", courseService.searchAndFilterCourses(
            searchTerm,
            categoryIds,
            levelIds,
            priceIds,
            pageable
        ));

        // Cargar los filtros
        model.addAttribute("allCategories", categoryService.findAll());
        model.addAttribute("allLevels", levelService.findAll());
        model.addAttribute("allPrices", priceService.findAll());

        // Devolver los filtros seleccionados
        model.addAttribute("selectedSearchTerm", searchTerm);
        model.addAttribute("selectedCategories", categoryIds != null ? categoryIds : List.of());
        model.addAttribute("selectedLevels", levelIds != null ? levelIds : List.of());
        model.addAttribute("selectedPrices", priceIds != null ? priceIds : List.of());
        model.addAttribute("selectedSort", sortParam);

        return "courses/index";
    }

    @GetMapping("/{slug}")
    public String show(
        @PathVariable String slug,
        Model model
    )
    {
        return "courses/show";
    }

}