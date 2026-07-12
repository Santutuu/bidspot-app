package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.subasta.CategoriaSubastasDTO;
import com.subastas.subastas_api.DTO.subasta.DetalleSubastaDTO;
import com.subastas.subastas_api.DTO.subasta.SubastaHomeDTO;
import com.subastas.subastas_api.model.Categoria;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.service.CategoriaService;
import com.subastas.subastas_api.service.DetalleSubastaService;
import com.subastas.subastas_api.service.SubastaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subastas")
public class SubastaController {

    private final SubastaService subastaService;
    private final DetalleSubastaService detalleSubastaService;
    private final CategoriaService categoriaService;
    private final UsuarioRepository usuarioRepository;

    public SubastaController(SubastaService subastaService,
                             DetalleSubastaService detalleSubastaService,
                             CategoriaService categoriaService,
                             UsuarioRepository usuarioRepository) {
        this.subastaService = subastaService;
        this.detalleSubastaService = detalleSubastaService;
        this.categoriaService = categoriaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/recomendadas")
    public ResponseEntity<List<SubastaHomeDTO>> obtenerRecomendadas(
            @RequestParam(defaultValue = "4") int limit
    ) {
        return ResponseEntity.ok(subastaService.obtenerSubastasRecomendadas(limit));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<CategoriaSubastasDTO> obtenerSubastasPorCategoria(
            @PathVariable Categoria categoria
    ) {
        return ResponseEntity.ok(categoriaService.obtenerSubastasPorCategoria(categoria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleSubastaDTO> obtenerDetalleSubasta(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Usuario usuarioActual = obtenerUsuarioActual(authentication);

        DetalleSubastaDTO response =
                detalleSubastaService.obtenerDetalle(id, usuarioActual);

        return ResponseEntity.ok(response);
    }

    private Usuario obtenerUsuarioActual(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        return usuarioRepository.findByMail(authentication.getName())
                .orElse(null);
    }
}