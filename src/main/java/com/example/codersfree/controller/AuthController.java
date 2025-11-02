package com.example.codersfree.controller;

import com.example.codersfree.annotation.Guest;
import com.example.codersfree.dto.UserDto;
import com.example.codersfree.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    @Guest
    public String mostrarLogin() {
        return "auth/login";
    }

    @GetMapping("/register")
    @Guest
    public String mostrarRegistro(Model model) {

        if (!model.containsAttribute("userDto")) {
            model.addAttribute("userDto", new UserDto());
        }

        return "auth/register";
    }

    @PostMapping("/register")
    @Guest
    public String registrarUsuario(
        @Valid @ModelAttribute("userDto") UserDto userDto,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes) {

        try {
            //Registrar el usuario
            authService.registrar(userDto);

            //Autenticar al usuario recién registrado
            authService.autenticar(userDto.getEmail(), userDto.getPassword());

            redirectAttributes.addFlashAttribute(
                "success", 
                "¡Tu cuenta ha sido creada e iniciada correctamente!"
            );
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                "error", 
                e.getMessage()
            );
            redirectAttributes.addFlashAttribute("user", userDto);

            return "redirect:/register";
        }
    }

    @GetMapping("/debug-auth")
    @ResponseBody
    public String debugAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Usuario: " + auth.getName() + ", Autenticado: " + auth.isAuthenticated();
    }
}