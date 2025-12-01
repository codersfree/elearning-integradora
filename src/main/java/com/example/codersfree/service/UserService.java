package com.example.codersfree.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.codersfree.dto.UserDto;
import com.example.codersfree.dto.UserUpdateDto;
import com.example.codersfree.model.Role;
import com.example.codersfree.model.User;
import com.example.codersfree.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // INYECCIÓN DEL ROLE SERVICE (Asumiendo que RoleService está definido)
    @Autowired
    private RoleService roleService; 

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
    public Page<User> paginate(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public User save(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        
        // CONVERSIÓN: IDs de roles a Entidades Role usando RoleService
        Set<Role> assignedRoles = userDto.getRoles().stream()
                .map(roleId -> roleService.findById(roleId))
                .collect(Collectors.toSet());

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword())) 
                .roles(assignedRoles) // Asignar roles
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User update(Long id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        if (!user.getEmail().equals(userUpdateDto.getEmail()) && userRepository.existsByEmail(userUpdateDto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado por otro usuario.");
        }

        // CONVERSIÓN: IDs de roles a Entidades Role usando RoleService
        Set<Role> assignedRoles = userUpdateDto.getRoles().stream()
                .map(roleId -> roleService.findById(roleId))
                .collect(Collectors.toSet());
        
        user.setName(userUpdateDto.getName());
        user.setEmail(userUpdateDto.getEmail());
        user.setRoles(assignedRoles); // Actualizar roles
        
        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}