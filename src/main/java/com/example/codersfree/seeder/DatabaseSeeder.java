package com.example.codersfree.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserSeeder userSeeder;
    @Autowired
    private CategorySeeder categorySeeder;
    @Autowired
    private LevelSeeder levelSeeder;
    @Autowired
    private PriceSeeder priceSeeder;
    @Autowired
    private CourseSeeder courseSeeder;
    @Autowired
    private CoursesByInstructorSeeder coursesByInstructorSeeder;

    @Override
    public void run(String... args) throws Exception {

        eliminarUploads();

        userSeeder.seed();
        categorySeeder.seed();
        levelSeeder.seed();
        priceSeeder.seed();
        courseSeeder.seed();
        coursesByInstructorSeeder.seed();

        System.out.println("Sembrado completado.");
    }

    private void eliminarUploads() {
        File uploads = new File("uploads");
        if (uploads.exists()) {
            FileSystemUtils.deleteRecursively(uploads);
            System.out.println("Carpeta 'uploads' eliminada.");
        } else {
            System.out.println("No existe la carpeta 'uploads'.");
        }
    }
}
