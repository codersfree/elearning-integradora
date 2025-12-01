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

import com.example.codersfree.dto.LevelDto;
import com.example.codersfree.model.Level;
import com.example.codersfree.service.LevelService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/levels")
public class LevelAdminController {

    @Autowired
    private LevelService levelService;
    
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
        model.addAttribute("levels", levelService.paginate(pageable));
        return "admin/levels/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        if (!model.containsAttribute("levelDto")) {
            model.addAttribute("levelDto", new LevelDto());
        }
        return "admin/levels/create";
    }

    @PostMapping("/create")
    public String store(
        @Valid @ModelAttribute("levelDto") LevelDto levelDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.levelDto",
                bindingResult
            );
            redirectAttributes.addFlashAttribute("levelDto", levelDto);
            return "redirect:/admin/levels/create"; 
        }

        try {
            levelService.save(levelDto);
            redirectAttributes.addFlashAttribute("success", "¡Nivel creado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el nivel: " + e.getMessage());
        }

        return "redirect:/admin/levels";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {
        Level level = levelService.findById(id);

        if (!model.containsAttribute("levelDto")) {
            LevelDto levelDto = new LevelDto(level.getName());
            model.addAttribute("levelDto", levelDto);
        }
        model.addAttribute("level", level);
        return "admin/levels/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(
        @PathVariable("id") Long id, 
        @Valid @ModelAttribute("levelDto") LevelDto levelDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.levelDto",
                bindingResult
            );
            redirectAttributes.addFlashAttribute("levelDto", levelDto);
            return "redirect:/admin/levels/" + id + "/edit"; 
        }

        try {
            levelService.update(id, levelDto);
            redirectAttributes.addFlashAttribute("success", "¡Nivel actualizado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el nivel: " + e.getMessage());
        }

        return "redirect:/admin/levels/" + id + "/edit";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            levelService.delete(id);
            redirectAttributes.addFlashAttribute("success", "¡Nivel eliminado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el nivel: " + e.getMessage());
        }
        return "redirect:/admin/levels";
    }
}