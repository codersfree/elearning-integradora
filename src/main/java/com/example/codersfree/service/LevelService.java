
package com.example.codersfree.service;

import com.example.codersfree.model.Level;
import com.example.codersfree.repository.LevelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Level findById(Long id) {
        return levelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nivel no encontrado con id: " + id));
    }
}
