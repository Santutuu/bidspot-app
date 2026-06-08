package com.subastas.subastas_api.DTO;

public class UploadResponseDTO {

    private String url;

    public UploadResponseDTO(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}