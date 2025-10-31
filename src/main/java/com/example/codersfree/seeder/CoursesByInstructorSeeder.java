package com.example.codersfree.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Component
public class CoursesByInstructorSeeder implements Seeder {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void seed() {
        System.out.println("Creando procedimiento almacenado: FindByInstructorEmail...");

        entityManager.createNativeQuery("DROP PROCEDURE IF EXISTS FindByInstructorEmail").executeUpdate();

        String procedure = """
            CREATE PROCEDURE FindByInstructorEmail(IN instructor_email VARCHAR(45))
            BEGIN
                DECLARE instructor_id BIGINT DEFAULT NULL;

                SELECT id INTO instructor_id
                FROM users
                WHERE email = instructor_email
                LIMIT 1;

                SELECT *
                FROM courses
                WHERE user_id = instructor_id;
            END
        """;

        entityManager.createNativeQuery(procedure).executeUpdate();

        System.out.println("✅ Procedimiento FindByInstructorEmail creado correctamente.");
    }
}
