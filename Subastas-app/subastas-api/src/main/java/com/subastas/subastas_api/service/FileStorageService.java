package com.subastas.subastas_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    public String saveDniImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo es obligatorio");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Solo se permiten imágenes");
        }

        try {
            Path dniFolder = Paths.get(uploadDir, "dni");
            Files.createDirectories(dniFolder);

            String originalName = file.getOriginalFilename();
            String extension = getExtension(originalName);

            String filename = UUID.randomUUID() + extension;

            Path destination = dniFolder.resolve(filename);

            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + "/uploads/dni/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar la imagen");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }

        return filename.substring(filename.lastIndexOf("."));
    }
}