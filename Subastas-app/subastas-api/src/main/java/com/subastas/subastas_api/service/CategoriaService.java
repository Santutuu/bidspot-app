package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.subasta.CategoriaSubastasDTO;
import com.subastas.subastas_api.DTO.subasta.SubastaHomeDTO;
import com.subastas.subastas_api.mapper.SubastaMapper;
import com.subastas.subastas_api.model.Categoria;
import com.subastas.subastas_api.model.EstadoSubasta;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.repository.SubastaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final SubastaRepository subastaRepository;
    private final SubastaMapper subastaMapper;

    public CategoriaService(SubastaRepository subastaRepository,
                            SubastaMapper subastaMapper) {
        this.subastaRepository = subastaRepository;
        this.subastaMapper = subastaMapper;
    }

    public CategoriaSubastasDTO obtenerSubastasPorCategoria(Categoria categoria) {

        List<Subasta> activas =
                subastaRepository.findByItemCategoriaAndEstadoSubasta(
                        categoria,
                        EstadoSubasta.ACTIVA
                );

        List<Subasta> programadas =
                subastaRepository.findByItemCategoriaAndEstadoSubasta(
                        categoria,
                        EstadoSubasta.PROGRAMADA
                );

        List<SubastaHomeDTO> activasDTO = activas.stream()
                .map(subastaMapper::toHomeDTO)
                .toList();

        List<SubastaHomeDTO> programadasDTO = programadas.stream()
                .map(subastaMapper::toHomeDTO)
                .toList();

        return new CategoriaSubastasDTO(
                activasDTO,
                programadasDTO
        );
    }
}