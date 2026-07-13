package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.admin.VencimientoCompraResponseDTO;
import com.subastas.subastas_api.service.VencimientoCompraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/compras")
public class AdminCompraController {

    private final VencimientoCompraService
            vencimientoCompraService;

    public AdminCompraController(
            VencimientoCompraService vencimientoCompraService
    ) {
        this.vencimientoCompraService =
                vencimientoCompraService;
    }

    @PostMapping("/procesar-vencimientos")
    public ResponseEntity<VencimientoCompraResponseDTO>
    procesarVencimientos() {

        VencimientoCompraResponseDTO resultado =
                vencimientoCompraService
                        .procesarVencimientos();

        return ResponseEntity.ok(
                resultado
        );
    }
}