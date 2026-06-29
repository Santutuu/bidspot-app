package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.subasta.DetalleSubastaDTO;
import com.subastas.subastas_api.mapper.SubastaMapper;
import com.subastas.subastas_api.model.EstadoSubasta;
import com.subastas.subastas_api.model.EstadoUsuario;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.SubastaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DetalleSubastaService {

    private final SubastaRepository subastaRepository;
    private final SubastaMapper subastaMapper;

    public DetalleSubastaService(SubastaRepository subastaRepository,
                                 SubastaMapper subastaMapper) {
        this.subastaRepository = subastaRepository;
        this.subastaMapper = subastaMapper;
    }

    public DetalleSubastaDTO obtenerDetalleSubasta(Long idSubasta,
                                                   Usuario usuarioActual) {

        Subasta subasta = subastaRepository.findById(idSubasta)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una subasta con id " + idSubasta
                ));

        validarSubastaVisible(subasta);
        validarAccesoDetalle(subasta, usuarioActual);

        return subastaMapper.toDetalleDTO(subasta, usuarioActual);
    }

    private void validarSubastaVisible(Subasta subasta) {
        if (subasta.getEstadoSubasta() == EstadoSubasta.FINALIZADA ||
                subasta.getEstadoSubasta() == EstadoSubasta.CANCELADA) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La subasta no se encuentra disponible"
            );
        }
    }

    private void validarAccesoDetalle(Subasta subasta, Usuario usuarioActual) {
        if (usuarioActual == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesión para ver el detalle de la subasta"
            );
        }

        if (usuarioActual.getEstado() != EstadoUsuario.VALIDADO) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no está validado"
            );
        }

        if (usuarioActual.getPassword() == null || usuarioActual.getPassword().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario todavía no completó su contraseña"
            );
        }

        if (usuarioActual.getCategoria() == null ||
                usuarioActual.getCategoria().ordinal() < subasta.getCategoriaMin().ordinal()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La categoría del usuario no permite acceder a esta subasta"
            );
        }
    }
}