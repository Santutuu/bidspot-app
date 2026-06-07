package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.CategoriaSubastasDTO;
import com.subastas.subastas_api.DTO.SubastaHomeDTO;
import com.subastas.subastas_api.model.Categoria;
import com.subastas.subastas_api.model.EstadoSubasta;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.SubastaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final SubastaRepository subastaRepository;

    public CategoriaService(SubastaRepository subastaRepository) {
        this.subastaRepository = subastaRepository;
    }

    public CategoriaSubastasDTO obtenerSubastasPorCategoria(Categoria categoria,
                                                            Usuario usuarioActual) {

        List<Subasta> tiempoReal =
                subastaRepository.findByItemCategoriaAndEstadoSubasta(
                        categoria,
                        EstadoSubasta.ACTIVA
                );

        List<Subasta> programadas =
                subastaRepository.findByItemCategoriaAndEstadoSubasta(
                        categoria,
                        EstadoSubasta.CREADA
                );

        boolean usuarioAutenticado = usuarioActual != null;

        List<SubastaHomeDTO> tiempoRealDTO = tiempoReal.stream()
                .filter(subasta -> noEsPropia(subasta, usuarioActual))
                .map(subasta -> SubastaHomeDTO.fromEntity(subasta, usuarioAutenticado))
                .toList();

        List<SubastaHomeDTO> programadasDTO = programadas.stream()
                .filter(subasta -> noEsPropia(subasta, usuarioActual))
                .map(subasta -> SubastaHomeDTO.fromEntity(subasta, usuarioAutenticado))
                .toList();

        return new CategoriaSubastasDTO(
                categoria.toString(),
                tiempoRealDTO,
                programadasDTO
        );
    }

    private boolean noEsPropia(Subasta subasta, Usuario usuarioActual) {
        return usuarioActual == null
                || subasta.getDuenio() == null
                || !subasta.getDuenio().getIdUsuario().equals(usuarioActual.getIdUsuario());
    }
}