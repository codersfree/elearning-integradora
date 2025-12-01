package com.example.codersfree.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class VideoDurationService {

    private static final Logger logger = LoggerFactory.getLogger(VideoDurationService.class);

    @Value("${ffmpeg.path.binary:ffmpeg}") 
    private String ffmpegBinaryPath; 

    public Integer getDurationInSeconds(MultipartFile file) {
        
        File tempFile = null;
        try {
            String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("temp_video");
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }
            
            tempFile = File.createTempFile("upload-", extension);
            file.transferTo(tempFile);
            logger.info("FFMPEG: Archivo temporal creado en: {}", tempFile.getAbsolutePath());

            // --- EJECUCIÓN FFmpeg ---
            ProcessBuilder pb = new ProcessBuilder(
                ffmpegBinaryPath, 
                "-i", 
                tempFile.getAbsolutePath()
            );
            
            Process process = pb.start();
            
            // Lógica para prevenir deadlock y leer salida
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            String durationLine = null;

            while ((line = reader.readLine()) != null) {
                if (line.contains("Duration:")) {
                    durationLine = line;
                    break;
                }
            }
            
            process.waitFor(10, TimeUnit.SECONDS);

            // --- PARSEAR DURACIÓN ---
            if (durationLine != null) {
                String durationString = durationLine.split("Duration:")[1].trim().split(",")[0].trim();
                Integer duration = parseDuration(durationString);
                logger.info("FFMPEG: Duración extraída: {} segundos.", duration);
                return duration;
            }
            
            logger.warn("FFMPEG: No se pudo encontrar la duración en la salida de FFmpeg. Devolviendo 0.");
            return 0; 

        } catch (Exception e) {
            logger.error("Error crítico al ejecutar FFmpeg. Asegurese que la ruta ({}) es correcta. Causa: {}", ffmpegBinaryPath, e.getMessage(), e);
            return 0;
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                    logger.info("FFMPEG: Archivo temporal limpiado.");
                } catch (IOException e) {
                    logger.warn("No se pudo eliminar el archivo temporal: {}", e.getMessage());
                }
            }
        }
    }

    private Integer parseDuration(String durationString) {
        try {
            if (durationString.contains(".")) {
                 durationString = durationString.split("\\.")[0];
            }
            
            String[] parts = durationString.split(":");
            if (parts.length < 3) return 0;

            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);

            return (hours * 3600) + (minutes * 60) + seconds;
            
        } catch (Exception e) {
            logger.error("Fallo al parsear la cadena de duración: {}", durationString, e);
            return 0;
        }
    }
}