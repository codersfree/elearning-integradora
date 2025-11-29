package com.example.codersfree.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.codersfree.model.Role;
import com.example.codersfree.model.User;
import com.example.codersfree.repository.UserRepository;

import com.example.codersfree.web.util.PageWrapper;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + email));
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PageWrapper<User> findPaginate(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return new PageWrapper<>(users);
    }

}