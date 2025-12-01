package com.example.codersfree.service;

import com.example.codersfree.dto.CategoryDto;
import com.example.codersfree.model.Category;
import com.example.codersfree.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Category> paginate(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con id: " + id));
    }

    @Transactional
    public Category save(CategoryDto categoryDto) {
        // Opcional: Validar si ya existe el nombre
        // if (categoryRepository.existsByName(categoryDto.getName())) {
        //     throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
        // }

        Category category = Category.builder()
                .name(categoryDto.getName())
                .build();

        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, CategoryDto categoryDto) {
        Category category = findById(id); // Esto ya lanza excepción si no existe

        category.setName(categoryDto.getName());
        
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = findById(id); // Verificamos existencia antes de borrar
        categoryRepository.delete(category);
    }
}