package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.penalizacion.PagarPenalizacionRequestDTO;
import com.subastas.subastas_api.DTO.penalizacion.PenalizacionResponseDTO;
import com.subastas.subastas_api.service.PenalizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/me/penalizaciones")
public class PenalizacionController {

    private final PenalizacionService
            penalizacionService;

    public PenalizacionController(
            PenalizacionService penalizacionService
    ) {
        this.penalizacionService =
                penalizacionService;
    }

    @GetMapping
    public ResponseEntity<List<PenalizacionResponseDTO>>
    listarMisPenalizaciones(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                penalizacionService
                        .listarMisPenalizaciones(
                                authentication.getName()
                        )
        );
    }

    @GetMapping("/{idPenalizacion}")
    public ResponseEntity<PenalizacionResponseDTO>
    obtenerPenalizacion(
            Authentication authentication,
            @PathVariable Long idPenalizacion
    ) {
        return ResponseEntity.ok(
                penalizacionService
                        .obtenerPenalizacion(
                                authentication.getName(),
                                idPenalizacion
                        )
        );
    }

    @PostMapping("/{idPenalizacion}/pagar")
    public ResponseEntity<PenalizacionResponseDTO>
    pagarPenalizacion(
            Authentication authentication,
            @PathVariable Long idPenalizacion,
            @RequestBody
            PagarPenalizacionRequestDTO request
    ) {
        return ResponseEntity.ok(
                penalizacionService
                        .pagarPenalizacion(
                                authentication.getName(),
                                idPenalizacion,
                                request
                        )
        );
    }
}