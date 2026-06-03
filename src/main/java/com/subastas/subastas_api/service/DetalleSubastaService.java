package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.DetalleSubastaDTO;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.repository.SubastaRepository;
import org.springframework.stereotype.Service;

@Service
public class DetalleSubastaService {

    private final SubastaRepository subastaRepository;

    public DetalleSubastaService(SubastaRepository subastaRepository) {
        this.subastaRepository = subastaRepository;
    }

    public DetalleSubastaDTO obtenerDetalleSubasta(Long idArticulo) {

        Subasta subasta = subastaRepository.findByItemIdItem(idArticulo);

        return DetalleSubastaDTO.fromEntity(subasta);
    }
}