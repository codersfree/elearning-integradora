package com.example.codersfree.config;

import com.example.codersfree.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        // 1. REGLAS ESPECÍFICAS POR ROL (Las más restrictivas van PRIMERO)
                        
                        // Solo usuarios con el permiso exacto "Administrador" pueden entrar a /admin
                        .requestMatchers("/admin/**").hasAuthority("Administrador")
                        
                        // Solo usuarios con el permiso exacto "Instructor" pueden entrar a /instructor
                        .requestMatchers("/instructor/**").hasAuthority("Instructor")
                        
                        // 2. REGLAS GENERALES (Requieren estar logueado, sin importar el rol)
                        // El checkout, el proceso de pago y el reproductor del curso
                        .requestMatchers("/checkout/**", "/courses/*/learn/**", "/courses/my-courses").authenticated()
                        
                        // 3. REGLAS PÚBLICAS (Todo lo demás)
                        // Home, Catálogo, Login, Registro, Detalle de curso público
                        .anyRequest().permitAll())
                
                .csrf(csrf -> csrf
                        // Ignoramos CSRF para la respuesta de Izipay y APIs externas si las hubiera
                        .ignoringRequestMatchers("/api/**", "/checkout/process"))
                
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/login?error=true")
                        .permitAll())
                
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret") // clave usada para firmar la cookie
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // duración: 7 días
                        .rememberMeParameter("remember-me") // nombre del checkbox en el form
                )
                
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                
                .build();
    }
}