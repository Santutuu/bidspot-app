package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.subasta.SubastaHomeDTO;
import com.subastas.subastas_api.mapper.SubastaMapper;
import com.subastas.subastas_api.repository.SubastaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubastaService {

    private final SubastaRepository subastaRepository;
    private final SubastaMapper subastaMapper;

    public SubastaService(SubastaRepository subastaRepository,
                          SubastaMapper subastaMapper) {
        this.subastaRepository = subastaRepository;
        this.subastaMapper = subastaMapper;
    }

    public List<SubastaHomeDTO> obtenerSubastasRecomendadas(int limit) {
        return subastaRepository.findRandomActivas(PageRequest.of(0, limit))
                .stream()
                .map(subastaMapper::toHomeDTO)
                .toList();
    }
}