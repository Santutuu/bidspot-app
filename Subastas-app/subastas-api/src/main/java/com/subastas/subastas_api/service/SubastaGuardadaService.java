package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.subasta.SubastaHomeDTO;
import com.subastas.subastas_api.mapper.SubastaMapper;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.SubastaRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SubastaGuardadaService {

    private final UsuarioRepository usuarioRepository;
    private final SubastaRepository subastaRepository;
    private final SubastaMapper subastaMapper;

    public SubastaGuardadaService(UsuarioRepository usuarioRepository,
                                  SubastaRepository subastaRepository,
                                  SubastaMapper subastaMapper) {
        this.usuarioRepository = usuarioRepository;
        this.subastaRepository = subastaRepository;
        this.subastaMapper = subastaMapper;
    }

    public List<SubastaHomeDTO> obtenerGuardadas(Usuario usuario) {
        return usuario.getGuardadas()
                .stream()
                .map(subastaMapper::toHomeDTO)
                .toList();
    }

    public void guardarSubasta(Usuario usuario, Long idSubasta) {
        Subasta subasta = obtenerSubasta(idSubasta);

        usuario.guardarSubasta(subasta);
        usuarioRepository.save(usuario);
    }

    public void quitarSubasta(Usuario usuario, Long idSubasta) {
        Subasta subasta = obtenerSubasta(idSubasta);

        usuario.eliminarSubasta(subasta);
        usuarioRepository.save(usuario);
    }

    private Subasta obtenerSubasta(Long idSubasta) {
        return subastaRepository.findById(idSubasta)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una subasta con id " + idSubasta
                ));
    }
}