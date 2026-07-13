package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.mensajeria.UltimaAdjudicacionResponseDTO;
import com.subastas.subastas_api.service.MensajeriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/mensajeria")
public class MensajeriaController {

    private final MensajeriaService
            mensajeriaService;

    public MensajeriaController(
            MensajeriaService mensajeriaService
    ) {
        this.mensajeriaService =
                mensajeriaService;
    }

    @GetMapping("/ultima-adjudicacion")
    public ResponseEntity<UltimaAdjudicacionResponseDTO>
    obtenerUltimaAdjudicacion(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                mensajeriaService
                        .obtenerUltimaAdjudicacion(
                                authentication.getName()
                        )
        );
    }
}