package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.venta.*;
import com.subastas.subastas_api.service.CompraService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/me/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(
            CompraService compraService
    ) {
        this.compraService =
                compraService;
    }

    @GetMapping
    public ResponseEntity<List<VentaResumenResponseDTO>>
    listarMisCompras(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                compraService.listarMisCompras(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<VentaDetalleResponseDTO>
    obtenerDetalleCompra(
            Authentication authentication,
            @PathVariable Long idVenta
    ) {
        return ResponseEntity.ok(
                compraService.obtenerDetalleCompra(
                        authentication.getName(),
                        idVenta
                )
        );
    }

    @PostMapping("/{idVenta}/entrega")
    public ResponseEntity<VentaDetalleResponseDTO>
    configurarEntrega(
            Authentication authentication,
            @PathVariable Long idVenta,
            @RequestBody
            ConfigurarEntregaRequestDTO request
    ) {
        return ResponseEntity.ok(
                compraService.configurarEntrega(
                        authentication.getName(),
                        idVenta,
                        request
                )
        );
    }

    @PostMapping("/{idVenta}/medio-pago")
    public ResponseEntity<VentaDetalleResponseDTO>
    seleccionarMedioPago(
            Authentication authentication,
            @PathVariable Long idVenta,
            @RequestBody
            SeleccionarMedioPagoRequestDTO request
    ) {
        return ResponseEntity.ok(
                compraService.seleccionarMedioPago(
                        authentication.getName(),
                        idVenta,
                        request
                )
        );
    }

    @PostMapping("/{idVenta}/confirmar")
    public ResponseEntity<VentaDetalleResponseDTO>
    confirmarCompra(
            Authentication authentication,
            @PathVariable Long idVenta,
            @RequestBody
            ConfirmarCompraRequestDTO request
    ) {
        return ResponseEntity.ok(
                compraService.confirmarCompra(
                        authentication.getName(),
                        idVenta,
                        request
                )
        );
    }

    @GetMapping("/{idVenta}/estado")
    public ResponseEntity<VentaDetalleResponseDTO>
    obtenerEstadoCompra(
            Authentication authentication,
            @PathVariable Long idVenta
    ) {
        return ResponseEntity.ok(
                compraService.obtenerEstadoCompra(
                        authentication.getName(),
                        idVenta
                )
        );
    }

    @GetMapping("/{idVenta}/factura")
    public ResponseEntity<FacturaResponseDTO>
    obtenerFactura(
            Authentication authentication,
            @PathVariable Long idVenta
    ) {
        return ResponseEntity.ok(
                compraService.obtenerFactura(
                        authentication.getName(),
                        idVenta
                )
        );
    }
}