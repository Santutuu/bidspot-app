package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.DetalleSubastaDTO;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.repository.SubastaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DetalleSubastaService {

    private final SubastaRepository subastaRepository;

    public DetalleSubastaService(SubastaRepository subastaRepository) {
        this.subastaRepository = subastaRepository;
    }

    public DetalleSubastaDTO obtenerDetalleSubasta(Long idSubasta) {

        Subasta subasta = subastaRepository.findById(idSubasta)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una subasta con id " + idSubasta
                ));

        return DetalleSubastaDTO.fromEntity(subasta);
    }
}