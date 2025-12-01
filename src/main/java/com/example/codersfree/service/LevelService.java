package com.example.codersfree.service;

import com.example.codersfree.dto.LevelDto;
import com.example.codersfree.model.Level;
import com.example.codersfree.repository.LevelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LevelService {

    @Autowired
    private LevelRepository levelRepository;

    @Transactional(readOnly = true)
    public List<Level> findAll() {
        return levelRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Level> paginate(Pageable pageable) {
        return levelRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Level findById(Long id) {
        return levelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nivel no encontrado con id: " + id));
    }

    @Transactional
    public Level save(LevelDto levelDto) {
        Level level = Level.builder()
                .name(levelDto.getName())
                .build();
        return levelRepository.save(level);
    }

    @Transactional
    public Level update(Long id, LevelDto levelDto) {
        Level level = findById(id); // Lanza excepción si no existe
        level.setName(levelDto.getName());
        return levelRepository.save(level);
    }

    @Transactional
    public void delete(Long id) {
        Level level = findById(id); // Verificamos existencia
        levelRepository.delete(level);
    }
}