package com.example.codersfree.seeder;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.codersfree.model.Category;
import com.example.codersfree.repository.CategoryRepository;

@Component
public class CategorySeeder implements Seeder {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void seed() {

        System.out.println("Sembrando categorías...");

        Category cat1 = Category.builder().name("Desarrollo Web").build();
        Category cat2 = Category.builder().name("Diseño Gráfico").build();
        Category cat3 = Category.builder().name("Marketing Digital").build();
        Category cat4 = Category.builder().name("Desarrollo Móvil").build();
        List<Category> categories = Arrays.asList(cat1, cat2, cat3, cat4);

        categoryRepository.saveAll(categories);
    }
}
