package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.SubastaHomeDTO;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.SubastaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubastaService {

    private final SubastaRepository subastaRepository;

    public SubastaService(SubastaRepository subastaRepository) {
        this.subastaRepository = subastaRepository;
    }

    public List<SubastaHomeDTO> obtenerSubastasRecomendadas(int limit, Usuario usuarioActual) {

        return subastaRepository.findRandomActivas(PageRequest.of(0, limit))
                .stream()

                // Regla: no mostrarle al usuario sus propias subastas
                .filter(subasta -> usuarioActual == null
                        || subasta.getDuenio() == null
                        || !subasta.getDuenio().getIdUsuario().equals(usuarioActual.getIdUsuario()))

                // Regla: si no está autenticado, no ve el precio actual
                .map(subasta -> SubastaHomeDTO.fromEntity(subasta, usuarioActual != null))

                .toList();
    }
}