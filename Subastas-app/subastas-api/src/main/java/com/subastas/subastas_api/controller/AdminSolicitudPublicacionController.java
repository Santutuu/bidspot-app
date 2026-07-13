package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.publicacion.ActualizarUbicacionRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.ConfirmarRecepcionRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.MostrarInteresesRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.ProponerCondicionesVentaRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.RechazarSolicitudRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionDetalleDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionResumenDTO;
import com.subastas.subastas_api.model.Categoria;
import com.subastas.subastas_api.model.EstadoSolicitud;
import com.subastas.subastas_api.service.AdminSolicitudPublicacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/solicitudes-publicacion")
public class AdminSolicitudPublicacionController {

    private final AdminSolicitudPublicacionService solicitudService;

    public AdminSolicitudPublicacionController(
            AdminSolicitudPublicacionService solicitudService
    ) {
        this.solicitudService = solicitudService;
    }

    @GetMapping
    public ResponseEntity<List<SolicitudPublicacionResumenDTO>>
    listarSolicitudes(
            @RequestParam(required = false)
            EstadoSolicitud estado,

            @RequestParam(required = false)
            Categoria categoria
    ) {
        return ResponseEntity.ok(
                solicitudService.listarSolicitudes(
                        estado,
                        categoria
                )
        );
    }

    @GetMapping("/{idSolicitud}")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    obtenerDetalle(
            @PathVariable Long idSolicitud
    ) {
        return ResponseEntity.ok(
                solicitudService.obtenerDetalle(
                        idSolicitud
                )
        );
    }

    @PostMapping("/{idSolicitud}/mostrar-interes")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    mostrarInteres(
            @PathVariable Long idSolicitud,
            @RequestBody MostrarInteresesRequestDTO request
    ) {
        return ResponseEntity.ok(
                solicitudService.mostrarInteres(
                        idSolicitud,
                        request
                )
        );
    }

    @PostMapping("/{idSolicitud}/confirmar-recepcion")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    confirmarRecepcion(
            @PathVariable Long idSolicitud,
            @RequestBody(required = false)
            ConfirmarRecepcionRequestDTO request
    ) {
        return ResponseEntity.ok(
                solicitudService.confirmarRecepcion(
                        idSolicitud,
                        request
                )
        );
    }

    @PostMapping("/{idSolicitud}/rechazar")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    rechazarSolicitud(
            @PathVariable Long idSolicitud,
            @RequestBody RechazarSolicitudRequestDTO request
    ) {
        return ResponseEntity.ok(
                solicitudService.rechazarSolicitud(
                        idSolicitud,
                        request
                )
        );
    }

    @PostMapping("/{idSolicitud}/condiciones-venta")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    proponerCondicionesVenta(
            @PathVariable Long idSolicitud,
            @RequestBody
            ProponerCondicionesVentaRequestDTO request
    ) {
        return ResponseEntity.ok(
                solicitudService.proponerCondicionesVenta(
                        idSolicitud,
                        request
                )
        );
    }

    @PostMapping("/{idSolicitud}/incorporar-subasta")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    incorporarASubasta(
            @PathVariable Long idSolicitud
    ) {
        return ResponseEntity.ok(
                solicitudService.incorporarASubasta(
                        idSolicitud
                )
        );
    }

    @PatchMapping("/{idSolicitud}/ubicacion")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    actualizarUbicacion(
            @PathVariable Long idSolicitud,
            @RequestBody
            ActualizarUbicacionRequestDTO request
    ) {
        return ResponseEntity.ok(
                solicitudService.actualizarUbicacion(
                        idSolicitud,
                        request
                )
        );
    }

    @PostMapping("/{idSolicitud}/devolucion/enviar")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    marcarDevolucionEnviada(
            @PathVariable Long idSolicitud
    ) {
        return ResponseEntity.ok(
                solicitudService.marcarDevolucionEnviada(
                        idSolicitud
                )
        );
    }

    @PostMapping("/{idSolicitud}/devolucion/confirmar-entrega")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    confirmarEntregaDevolucion(
            @PathVariable Long idSolicitud
    ) {
        return ResponseEntity.ok(
                solicitudService.confirmarEntregaDevolucion(
                        idSolicitud
                )
        );
    }
}