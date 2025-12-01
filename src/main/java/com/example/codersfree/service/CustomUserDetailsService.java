package com.example.codersfree.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    @Transactional(readOnly = true) // Importante para asegurar que se lean los roles
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Buscamos el usuario en la Base de Datos
        com.example.codersfree.model.User user = userService.findByEmail(email);
        
        // 2. Convertimos los Roles de la BD (Entity) a Autoridades de Spring Security
        // Asumimos que user.getRoles() devuelve los roles "Administrador", "Instructor", etc.
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) 
                .collect(Collectors.toList());

        // 3. Retornamos el usuario con las autoridades dinámicas
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities) // <--- USAMOS AUTHORITIES, NO ROLES
                .build();
    }
}