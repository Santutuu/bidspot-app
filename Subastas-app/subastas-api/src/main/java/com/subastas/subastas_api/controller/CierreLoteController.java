package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.subasta.CierreLoteResponseDTO;
import com.subastas.subastas_api.service.CierreLoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subastas/{idSubasta}/lotes/{idItemCatalogo}")
public class CierreLoteController {

    private final CierreLoteService cierreLoteService;

    public CierreLoteController(CierreLoteService cierreLoteService) {
        this.cierreLoteService = cierreLoteService;
    }

    @PostMapping("/cerrar")
    public ResponseEntity<CierreLoteResponseDTO> cerrarLote(
            @PathVariable Long idSubasta,
            @PathVariable Long idItemCatalogo
    ) {
        return ResponseEntity.ok(
                cierreLoteService.cerrarLote(idSubasta, idItemCatalogo)
        );
    }

    @PostMapping("/reabrir")
    public ResponseEntity<CierreLoteResponseDTO> reabrirLote(
            @PathVariable Long idSubasta,
            @PathVariable Long idItemCatalogo
    ) {
        return ResponseEntity.ok(
                cierreLoteService.reabrirLote(idSubasta, idItemCatalogo)
        );
    }
}