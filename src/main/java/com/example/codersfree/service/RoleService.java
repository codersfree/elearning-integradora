package com.example.codersfree.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.codersfree.dto.RoleDto;
import com.example.codersfree.model.Role;
import com.example.codersfree.repository.RoleRepository;
import com.example.codersfree.web.util.PageWrapper;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Role findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado con id: " + id));
        return role;
    }

    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PageWrapper<Role> findPaginate(Pageable pageable) {
        Page<Role> roles = roleRepository.findAll(pageable);
        return new PageWrapper<>(roles);
    }

    public Role save(RoleDto roleDto) {

        if (roleRepository.existsByName(roleDto.getName())) {
            throw new IllegalArgumentException("El rol ya existe");
        }

        Role role = Role.builder()
                .name(roleDto.getName())
                .build();

        return roleRepository.save(role);
    }

    public Role update(Long id, RoleDto roleDto) {

        Role role = findById(id);

        if (!role.getName().equals(roleDto.getName()) && roleRepository.existsByName(roleDto.getName())) {
            throw new IllegalArgumentException("El rol ya existe");
        }

        role.setName(roleDto.getName());

        return roleRepository.save(role);
    }

    public void delete(Long id) {
        Role role = findById(id);
        roleRepository.delete(role);
    }

}
