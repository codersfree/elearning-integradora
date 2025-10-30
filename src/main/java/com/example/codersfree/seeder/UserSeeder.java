package com.example.codersfree.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.codersfree.model.User;
import com.example.codersfree.repository.UserRepository;

@Component
public class UserSeeder implements Seeder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void seed() {
        System.out.println("Sembrando usuarios...");
        
        User user = User.builder()
                .name("Victor Arana")
                .email("victor@codersfree.com")
                .password(passwordEncoder.encode("12345678"))
                .build();
        userRepository.save(user);
    }
}
