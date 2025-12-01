package com.example.codersfree.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ThumbnailService {

    private static final Logger logger = LoggerFactory.getLogger(ThumbnailService.class);

    @Value("${ffmpeg.path.binary:ffmpeg}")
    private String ffmpegBinaryPath;

    private static final String BASE_PATH = "uploads/";

    public String generateThumbnail(String videoRelativePath, String targetDirectory) throws IOException {
        
        Path inputPath = Paths.get(BASE_PATH, videoRelativePath).normalize();
        
        String thumbnailFilename = UUID.randomUUID().toString() + ".jpg";
        Path outputDir = Paths.get(BASE_PATH, targetDirectory);
        Path outputPath = outputDir.resolve(thumbnailFilename);

        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
        
        // Comando FFmpeg: Capturar un frame en el segundo 5
        ProcessBuilder pb = new ProcessBuilder(
            ffmpegBinaryPath,
            "-i", inputPath.toAbsolutePath().toString(),
            "-ss", "00:00:05", // Capturar en el segundo 5
            "-vframes", "1", // Capturar solo un frame
            "-f", "image2", // Forzar formato de salida
            "-y", // Sobrescribir si existe
            outputPath.toAbsolutePath().toString()
        );
        
        logger.info("FFMPEG THUMBNAIL: Ejecutando comando para generar: {}", outputPath.toAbsolutePath());

        try {
            Process process = pb.start();
            
            // Clase auxiliar para consumir los streams del proceso en segundo plano (Anti-Deadlock)
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
            
            errorGobbler.start();
            outputGobbler.start();
            
            if (!process.waitFor(60, TimeUnit.SECONDS)) { 
                process.destroyForcibly();
                throw new IOException("FFmpeg timeout (60s) al generar miniatura.");
            }

            if (process.exitValue() != 0) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.error("FFMPEG ERROR OUTPUT: {}", line);
                }
                throw new IOException("FFmpeg falló al generar la miniatura. Código de salida: " + process.exitValue());
            }

            return targetDirectory + "/" + thumbnailFilename;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Proceso FFmpeg interrumpido.", e);
        }
    }
    
    private static class StreamGobbler extends Thread {
        private final InputStream is;
        private final String type;

        private StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (type.equals("ERROR")) {
                         logger.error("FFMPEG ERROR STREAM: {}", line);
                    }
                }
            } catch (IOException ioe) {
                logger.error("Error leyendo stream de FFmpeg: {}", ioe.getMessage());
            }
        }
    }
}