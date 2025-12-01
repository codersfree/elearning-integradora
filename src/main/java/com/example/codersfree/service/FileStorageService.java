package com.example.codersfree.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

// Importaciones para Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private static final String BASE_PATH = "uploads/";

    public String save(String directory, MultipartFile file) throws IOException {

        logger.info("FileStorageService: Iniciando guardado en directorio: {}", directory);

        // Validar que el archivo no esté vacío
        if (file.isEmpty()) {
            logger.error("FileStorageService: El archivo de entrada está vacío.");
            throw new IOException("El archivo está vacío.");
        }

        // Asegurarse de que el directorio termine con una barra
        if (!directory.endsWith("/")) {
            directory += "/";
        }

        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

        Path uploadDir = Paths.get(BASE_PATH + directory);
        logger.info("FileStorageService: Intentando crear directorios en: {}", uploadDir.toAbsolutePath());
        
        // Crea los directorios si no existen
        try {
            Files.createDirectories(uploadDir);
            logger.info("FileStorageService: Directorios creados/verificados con éxito.");
        } catch (AccessDeniedException e) {
            logger.error("FileStorageService: ERROR DE PERMISOS: El servicio no tiene permisos para crear directorios en: {}. Causa: {}", uploadDir.toAbsolutePath(), e.getMessage());
            throw e; // Relanzar la excepción para que sea manejada
        }

        Path filePath = uploadDir.resolve(filename);
        logger.info("FileStorageService: Ruta de destino final: {}", filePath.toAbsolutePath());
        
        // Copia el archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        logger.info("FileStorageService: Archivo copiado con éxito.");

        return directory + filename;
    }

    public boolean delete(String relativePath) throws IOException {

        if (relativePath == null || relativePath.isBlank()) return false;

        Path path = Paths.get(BASE_PATH).resolve(relativePath).normalize();

        logger.info("FileStorageService: Intentando eliminar archivo: {}", path.toAbsolutePath());
        boolean deleted = Files.deleteIfExists(path);
        logger.info("FileStorageService: Eliminación exitosa: {}", deleted);

        return deleted;
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex >= 0) ? filename.substring(dotIndex + 1) : "";
    }
}