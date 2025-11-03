package com.example.codersfree.controller.admin;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.codersfree.dto.CoverDto;
import com.example.codersfree.dto.RoleDto;
import com.example.codersfree.model.Cover;
import com.example.codersfree.service.CoverService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/covers")
public class CoverAdminController {

    @Autowired
    private CoverService coverService;

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

        model.addAttribute("covers", coverService.findPaginate(pageable));
        return "admin/covers/index";
        
    }

    @GetMapping("create")
    public String create(Model model) {

        if (!model.containsAttribute("coverDto")) {
            model.addAttribute("coverDto", new CoverDto());
        }

        return "admin/covers/create";
    }

    @PostMapping("create")
    public String store(
            @Valid @ModelAttribute("coverDto") CoverDto coverDto,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes
        ) {

        if (bindingResult.hasErrors()) {
            
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.coverDto",
                bindingResult
            );

            redirectAttributes.addFlashAttribute("coverDto", coverDto);

            return "redirect:/admin/covers/create";
        }

        try {
            
            Cover cover = coverService.save(coverDto, file);

            redirectAttributes.addFlashAttribute(
                "success", 
                "¡Portada creada! Ahora puedes añadir más detalles."
            );

            return "redirect:/admin/covers/" + cover.getId() + "/edit";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error", 
                e.getMessage()
            );

            redirectAttributes.addFlashAttribute("coverDto", coverDto);

            return "redirect:/admin/covers/create";
        }

    }

    @GetMapping("{id}/edit")
    public String edit(
        @PathVariable Long id, 
        Model model,
        RedirectAttributes redirectAttributes
    ) {

        Cover cover = coverService.findById(id);
        model.addAttribute("cover", cover);
        
        if (!model.containsAttribute("coverDto")) {            
            CoverDto coverDto = new CoverDto(cover);
            model.addAttribute("coverDto", coverDto);
        }

        return "admin/covers/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(
        @PathVariable("id") Long id, 
        @Valid @ModelAttribute("coverDto") CoverDto coverDto,
        @RequestParam("file") MultipartFile file,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.coverDto",
                bindingResult
            );

            redirectAttributes.addFlashAttribute("coverDto", coverDto);

            return "redirect:/admin/covers/" + id + "/edit";
        }

        try {
            coverService.update(id, coverDto, file);
            redirectAttributes.addFlashAttribute(
                "success", 
                "¡Portada actualizada correctamente!"
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error", 
                e.getMessage()
            );
        }

        return "redirect:/admin/covers/" + id + "/edit";
    }

    @PostMapping("{id}/delete")
    public String delete(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes
    ) {

        try {
            coverService.delete(id);

            redirectAttributes.addFlashAttribute(
                "success", 
                "¡Portada eliminada correctamente!"
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error", 
                e.getMessage()
            );
        }

        return "redirect:/admin/covers";
    }

}
