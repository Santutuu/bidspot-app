package com.subastas.subastas_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    public String saveDniImage(MultipartFile file) {
        return saveImage(file, "dni");
    }

    public List<String> saveSubastaImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new RuntimeException("Debe cargar al menos una imagen");
        }

        return files.stream()
                .map(this::saveSubastaImage)
                .toList();
    }

    public String saveSubastaImage(MultipartFile file) {
        return saveImage(file, "subastas");
    }

    private String saveImage(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo es obligatorio");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Solo se permiten imágenes");
        }

        try {
            Path folder = Paths.get(uploadDir, folderName);
            Files.createDirectories(folder);

            String originalName = file.getOriginalFilename();
            String extension = getExtension(originalName);

            String filename = UUID.randomUUID() + extension;

            Path destination = folder.resolve(filename);

            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + "/uploads/" + folderName + "/" + filename;

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