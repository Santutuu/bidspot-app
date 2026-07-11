package com.subastas.subastas_api.DTO.admin;

import com.subastas.subastas_api.model.CategoriaUsuario;

public class AprobarUsuarioRequestDTO {

    private CategoriaUsuario categoria;
    private Long idVerificador;

    public CategoriaUsuario getCategoria() {
        return categoria;
    }

    public Long getIdVerificador() {
        return idVerificador;
    }
}