package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.UploadResponseDTO;
import com.subastas.subastas_api.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    private final FileStorageService fileStorageService;

    public UploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/dni")
    public ResponseEntity<UploadResponseDTO> uploadDniImage(
            @RequestParam("file") MultipartFile file
    ) {
        String url = fileStorageService.saveDniImage(file);

        return ResponseEntity.ok(new UploadResponseDTO(url));
    }

    @PostMapping("/subastas")
    public ResponseEntity<List<String>> uploadSubastaImages(
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<String> urls = fileStorageService.saveSubastaImages(files);

        return ResponseEntity.ok(urls);
    }
}