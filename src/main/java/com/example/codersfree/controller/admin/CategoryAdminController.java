package com.example.codersfree.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.codersfree.dto.CategoryDto;
import com.example.codersfree.model.Category;
import com.example.codersfree.service.CategoryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categories")
public class CategoryAdminController {

    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public String index(
        Model model,
        @PageableDefault(
            size = 10, 
            page = 0,
            sort = "id",
            direction = Sort.Direction.DESC
        ) Pageable pageable
    ) {
        model.addAttribute("categories", categoryService.paginate(pageable));
        return "admin/categories/index";
    }

    @GetMapping("/create")
    public String create(Model model) {

        if (!model.containsAttribute("categoryDto")) {
            model.addAttribute("categoryDto", new CategoryDto());
        }

        return "admin/categories/create";
    }

    @PostMapping("/create")
    public String store(
        @Valid @ModelAttribute("categoryDto") CategoryDto categoryDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.categoryDto",
                bindingResult
            );

            redirectAttributes.addFlashAttribute("categoryDto", categoryDto);

            return "redirect:/admin/categories/create"; 
        }

        try {
            categoryService.save(categoryDto); // Asegúrate que tu servicio acepte el DTO o mapealo
            redirectAttributes.addFlashAttribute(
                "success", 
                "¡Categoría creada exitosamente!"
            );
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error", 
                "Error al crear la categoría: " + e.getMessage()
            );
        }

        return "redirect:/admin/categories";
    }

    @GetMapping("/{id}/edit")
    public String edit(
        @PathVariable("id") Long id, 
        Model model
    ) {

        Category category = categoryService.findById(id); // Asegúrate que tu servicio tenga findById

        if (!model.containsAttribute("categoryDto")) {
            CategoryDto categoryDto = new CategoryDto(category.getName());
            model.addAttribute("categoryDto", categoryDto);
        }

        model.addAttribute("category", category);

        return "admin/categories/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(
        @PathVariable("id") Long id, 
        @Valid @ModelAttribute("categoryDto") CategoryDto categoryDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.categoryDto",
                bindingResult
            );

            redirectAttributes.addFlashAttribute("categoryDto", categoryDto);

            return "redirect:/admin/categories/" + id + "/edit"; 
        }

        try {
            categoryService.update(id, categoryDto); // Asegúrate que tu servicio tenga update(id, dto)
            redirectAttributes.addFlashAttribute(
                "success", 
                "¡Categoría actualizada exitosamente!"
            );
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error", 
                "Error al actualizar la categoría: " + e.getMessage()
            );
        }

        return "redirect:/admin/categories/" + id + "/edit";
    }

    @PostMapping("/{id}/delete")
    public String delete(
        @PathVariable("id") Long id, 
        RedirectAttributes redirectAttributes
    ) {

        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute(
                "success", 
                "¡Categoría eliminada exitosamente!"
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error", 
                "Error al eliminar la categoría: " + e.getMessage()
            );
        }

        return "redirect:/admin/categories";
    }
}