package com.example.codersfree.controller.admin;

import com.example.codersfree.dto.UserDto;
import com.example.codersfree.dto.UserUpdateDto;
import com.example.codersfree.model.Role;
import com.example.codersfree.model.User;
import com.example.codersfree.service.RoleService; 
import com.example.codersfree.service.UserService;
import jakarta.validation.Valid;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService; // INYECCIÓN

    // R: Index (GET /admin/users)
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
        Page<User> users = userService.paginate(pageable);

        model.addAttribute("users", users);
        
        return "admin/users/index";
    }

    // C: Create Form (GET /admin/users/create)
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("allRoles", roleService.findAll()); // Enviar todos los roles
        return "admin/users/create";
    }

    // C: Store (POST /admin/users)
    @PostMapping
    public String store(@Valid UserDto userDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll()); // Recargar roles
            return "admin/users/create";
        }

        try {
            userService.save(userDto);
            redirectAttributes.addFlashAttribute("success", "Usuario creado con éxito.");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "email.exists", e.getMessage());
            model.addAttribute("allRoles", roleService.findAll()); // Recargar roles en caso de error
            return "admin/users/create";
        }
    }

    // U: Edit Form (GET /admin/users/{id}/edit)
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        // 1. Obtener los IDs de los roles actuales del usuario
        Set<Long> currentRoleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        // 2. Crear el DTO y asignar los IDs
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .roles(currentRoleIds) // Asignar IDs de roles
                .build();
        
        model.addAttribute("userId", id);
        model.addAttribute("userUpdateDto", userUpdateDto);
        model.addAttribute("allRoles", roleService.findAll()); // Enviar todos los roles
        
        return "admin/users/edit";
    }

    // U: Update (PUT /admin/users/{id})
    @PutMapping("/{id}") 
    public String update(@PathVariable Long id, 
                         @Valid UserUpdateDto userUpdateDto, 
                         BindingResult bindingResult, 
                         Model model,
                         RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            model.addAttribute("userUpdateDto", userUpdateDto); 
            model.addAttribute("allRoles", roleService.findAll()); // Recargar roles
            return "admin/users/edit";
        }

        try {
            userService.update(id, userUpdateDto);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado con éxito.");
            
            return "redirect:/admin/users/" + id + "/edit";

        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "email.exists", e.getMessage());
            
            model.addAttribute("userId", id);
            model.addAttribute("userUpdateDto", userUpdateDto);
            model.addAttribute("allRoles", roleService.findAll()); // Recargar roles
            return "admin/users/edit";
        }
    }

    // D: Delete (DELETE /admin/users/{id}/delete)
    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Usuario eliminado con éxito.");
        return "redirect:/admin/users";
    }
}