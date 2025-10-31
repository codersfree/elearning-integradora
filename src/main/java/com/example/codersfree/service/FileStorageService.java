package com.example.codersfree.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String BASE_PATH = "uploads/";

    public String save(String directory, MultipartFile file) throws IOException {

        // Validar que el archivo no esté vacío
        if (file.isEmpty()) {
            throw new IOException("El archivo está vacío.");
        }

        // Asegurarse de que el directorio termine con una barra
        if (!directory.endsWith("/")) {
            directory += "/";
        }

        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

        Path uploadDir = Paths.get(BASE_PATH + directory);
        Files.createDirectories(uploadDir);

        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return directory + filename;
    }

    public boolean delete(String relativePath) throws IOException {

        if (relativePath == null || relativePath.isBlank()) return false;

        Path path = Paths.get(BASE_PATH).resolve(relativePath).normalize();

        return Files.deleteIfExists(path);
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex >= 0) ? filename.substring(dotIndex + 1) : "";
    }
}