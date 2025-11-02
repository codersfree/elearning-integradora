package com.example.codersfree.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.codersfree.dto.RoleDto;
import com.example.codersfree.model.Role;
import com.example.codersfree.service.RoleService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class RoleAdminController {

    @Autowired
    private RoleService roleService;
    
    @GetMapping("/roles")
    public String index(Model model) {

        List<Role> roles = roleService.findAll();
        model.addAttribute("roles", roles);

        return "admin/roles/index";
    }

    @GetMapping("/roles/create")
    public String create(Model model) {

        if (!model.containsAttribute("roleDto")) {
            model.addAttribute("roleDto", new RoleDto());
        }

        return "admin/roles/create";
    }

    @PostMapping("/roles/create")
    public String store(
        @Valid @ModelAttribute("roleDto") RoleDto roleDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.roleDto",
                bindingResult
            );

            redirectAttributes.addFlashAttribute("roleDto", roleDto);

            return "redirect:/admin/roles/create"; 
        }

        try {
            roleService.save(roleDto);
            redirectAttributes.addFlashAttribute(
                "success", 
                "¡Rol creado exitosamente!"
            );
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error", 
                "Error al crear el rol: " + e.getMessage()
            );
        }

        return "redirect:/admin/roles";
    }

    @GetMapping("/roles/{id}/edit")
    public String edit(
        @PathVariable("id") Long id, 
        Model model
    ) {

        Role role = roleService.findById(id);

        if (!model.containsAttribute("roleDto")) {
            RoleDto roleDto = new RoleDto(role.getName());
            model.addAttribute("roleDto", roleDto);
        }

        model.addAttribute("role", role);

        return "admin/roles/edit";
    }

    @PostMapping("/roles/{id}/edit")
    public String edit(
        @PathVariable("id") Long id, 
        @Valid @ModelAttribute("roleDto") RoleDto roleDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.roleDto",
                bindingResult
            );

            redirectAttributes.addFlashAttribute("roleDto", roleDto);

            return "redirect:/admin/roles/" + id + "/edit"; 
        }

        try {
            roleService.update(id, roleDto);
            redirectAttributes.addFlashAttribute(
                "success", 
                "¡Rol actualizado exitosamente!"
            );
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error", 
                "Error al actualizar el rol: " + e.getMessage()
            );
        }

        return "redirect:/admin/roles/" + id + "/edit";
    }

    @PostMapping("/roles/{id}/delete")
    public String delete(
        @PathVariable("id") Long id, 
        RedirectAttributes redirectAttributes
    ) {

        try {
            roleService.delete(id);
            redirectAttributes.addFlashAttribute(
                "success", 
                "¡Rol eliminado exitosamente!"
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error", 
                "Error al eliminar el rol: " + e.getMessage()
            );
        }

        return "redirect:/admin/roles";
    }

}
