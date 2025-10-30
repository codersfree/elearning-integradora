package com.example.codersfree.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserSeeder userSeeder;
    private final CategorySeeder categorySeeder;
    private final LevelSeeder levelSeeder;
    private final PriceSeeder priceSeeder;
    private final CourseSeeder courseSeeder;

    public DatabaseSeeder(UserSeeder userSeeder, CategorySeeder categorySeeder,
                          LevelSeeder levelSeeder, PriceSeeder priceSeeder,
                          CourseSeeder courseSeeder) {
        this.userSeeder = userSeeder;
        this.categorySeeder = categorySeeder;
        this.levelSeeder = levelSeeder;
        this.priceSeeder = priceSeeder;
        this.courseSeeder = courseSeeder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Iniciando el sembrado de la base de datos...");

        File uploads = new File("uploads");
        if (uploads.exists()) {
            FileSystemUtils.deleteRecursively(uploads);
            System.out.println("Carpeta 'uploads' eliminada.");
        }

        userSeeder.seed();
        categorySeeder.seed();
        levelSeeder.seed();
        priceSeeder.seed();
        courseSeeder.seed();

        System.out.println("Sembrado completado.");
    }
}
