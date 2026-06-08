package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.CategoriaSubastasDTO;
import com.subastas.subastas_api.DTO.DetalleSubastaDTO;
import com.subastas.subastas_api.DTO.SubastaHomeDTO;
import com.subastas.subastas_api.model.Categoria;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.service.CategoriaService;
import com.subastas.subastas_api.service.DetalleSubastaService;
import com.subastas.subastas_api.service.SubastaService;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
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
            @RequestParam(defaultValue = "4") int limit,
            Authentication authentication
    ) {
        Usuario usuarioActual = obtenerUsuarioActual(authentication);

        List<SubastaHomeDTO> response =
                subastaService.obtenerSubastasRecomendadas(limit, usuarioActual);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<CategoriaSubastasDTO> obtenerSubastasPorCategoria(
            @PathVariable Categoria categoria,
            Authentication authentication
    ) {
        Usuario usuarioActual = obtenerUsuarioActual(authentication);

        CategoriaSubastasDTO response =
                categoriaService.obtenerSubastasPorCategoria(categoria, usuarioActual);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleSubastaDTO> obtenerDetalleSubasta(
            @PathVariable Long id
    ) {
        DetalleSubastaDTO response =
                detalleSubastaService.obtenerDetalleSubasta(id);

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