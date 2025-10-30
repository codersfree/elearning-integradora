package com.example.codersfree.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        com.example.codersfree.model.User user = userService.findByEmail(email);
        
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                // .disabled(false)
                .build();
    }
}