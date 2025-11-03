package com.example.codersfree.web.advice;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.codersfree.model.User;
import com.example.codersfree.repository.UserRepository;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("currentUrl")
    public String addCurrentUrlToModel(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("authUser")
    public User addAuthenticatedUserToModel(Authentication authentication) {

        // 1. Verifica si hay un usuario autenticado
        if (authentication != null && 
            authentication.isAuthenticated() && 
            !(authentication instanceof AnonymousAuthenticationToken)) {
            
            // 2. Obtiene el email (username) de la sesión de Spring
            String email = authentication.getName();
            
            // 3. ¡LLAMA A LA BASE DE DATOS!
            Optional<User> userOptional = userRepository.findByEmail(email);

            // 4. Devuelve el usuario si se encontró en la BBDD, si no, null.
            return userOptional.orElse(null);
        }
        
        // 5. Si no pasó la verificación, devuelve null.
        return null;
    }
}