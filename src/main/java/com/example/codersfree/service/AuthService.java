package com.example.codersfree.service;

import com.example.codersfree.dto.UserDto;
import com.example.codersfree.model.User;
import com.example.codersfree.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User registrar(UserDto userDto) {
        
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Este email ya está registrado");
        }

        String password = userDto.getPassword();

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(password))
                .build();
        
        userRepository.save(user);

        autenticar(user.getEmail(), password);

        return user;
    }

    private void autenticar(String email, String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr != null) {
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );
        } else {
            System.err.println("WARN: No se pudo obtener RequestAttributes para guardar en sesión.");
        }
    }
}