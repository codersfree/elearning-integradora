package com.example.codersfree.seeder;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.codersfree.model.Level;
import com.example.codersfree.repository.LevelRepository;

@Component
public class LevelSeeder implements Seeder {

    @Autowired
    private LevelRepository levelRepository;

    @Override
    public void seed() {

        System.out.println("Sembrando niveles...");

        Level lvl1 = Level.builder().name("Nivel Básico").build();
        Level lvl2 = Level.builder().name("Nivel Intermedio").build();
        Level lvl3 = Level.builder().name("Nivel Avanzado").build();
        List<Level> levels = Arrays.asList(lvl1, lvl2, lvl3);

        levelRepository.saveAll(levels);
    }
}
