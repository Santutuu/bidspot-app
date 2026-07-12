package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.subasta.DetalleSubastaDTO;
import com.subastas.subastas_api.mapper.SubastaMapper;
import com.subastas.subastas_api.model.CategoriaUsuario;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.SubastaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DetalleSubastaService {

    private final SubastaRepository subastaRepository;
    private final SubastaMapper subastaMapper;

    public DetalleSubastaService(
            SubastaRepository subastaRepository,
            SubastaMapper subastaMapper
    ) {
        this.subastaRepository = subastaRepository;
        this.subastaMapper = subastaMapper;
    }

    @Transactional(readOnly = true)
    public DetalleSubastaDTO obtenerDetalle(
            Long idSubasta,
            Usuario usuarioActual
    ) {
        Subasta subasta = subastaRepository
                .findById(idSubasta)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una subasta con id " + idSubasta
                ));

        validarAccesoDetalle(
                subasta,
                usuarioActual
        );

        return subastaMapper.toDetalleDTO(
                subasta,
                usuarioActual
        );
    }

    private void validarAccesoDetalle(
            Subasta subasta,
            Usuario usuarioActual
    ) {
        if (usuarioActual == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesión para acceder al detalle de la subasta"
            );
        }

        /*
         * Estado técnico de la cuenta.
         */
        if (usuarioActual.estaBloqueado()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La cuenta se encuentra bloqueada"
            );
        }

        /*
         * Validación comercial:
         *
         * - Persona.estadoRegistro debe ser VALIDADO.
         * - Debe existir Cliente.
         * - Cliente.admitido debe ser "si".
         */
        if (!usuarioActual.estaValidadoComoCliente()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario todavía no fue validado como cliente"
            );
        }

        CategoriaUsuario categoriaUsuario =
                usuarioActual.getCategoriaNegocio();

        if (categoriaUsuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El cliente todavía no tiene una categoría asignada"
            );
        }

        CategoriaUsuario categoriaMinima =
                subasta.getCategoriaMin();

        if (categoriaMinima != null
                && categoriaUsuario.ordinal()
                < categoriaMinima.ordinal()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La categoría del cliente no permite acceder a esta subasta"
            );
        }
    }
}