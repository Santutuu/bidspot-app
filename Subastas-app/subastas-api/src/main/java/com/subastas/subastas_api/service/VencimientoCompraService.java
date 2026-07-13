package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.admin.VencimientoCompraResponseDTO;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.EstadoVenta;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.model.VentaConcretada;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.repository.VentaConcretadaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VencimientoCompraService {

    private final VentaConcretadaRepository
            ventaRepository;

    private final UsuarioRepository
            usuarioRepository;

    public VencimientoCompraService(
            VentaConcretadaRepository ventaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.ventaRepository =
                ventaRepository;

        this.usuarioRepository =
                usuarioRepository;
    }

    /**
     * Procesa las compras cuyo plazo real fechaLimitePago venció.
     *
     * Este método puede llamarse inicialmente desde un endpoint
     * administrativo y posteriormente desde un scheduler.
     */
    @Transactional
    public VencimientoCompraResponseDTO
    procesarVencimientos() {

        List<VentaConcretada> ventasVencidas =
                ventaRepository
                        .findByEstadoAndFechaLimitePagoLessThanEqual(
                                EstadoVenta.PENDIENTE_PAGO,
                                LocalDateTime.now()
                        );

        Set<Long> idsUsuariosBloqueados =
                new HashSet<>();

        for (VentaConcretada venta
                : ventasVencidas) {

            procesarVenta(
                    venta,
                    idsUsuariosBloqueados
            );
        }

        /*
         * Aunque las ventas están administradas por JPA
         * y dirty checking sería suficiente, saveAll deja
         * explícita la persistencia del cambio.
         */
        ventaRepository.saveAll(
                ventasVencidas
        );

        return new VencimientoCompraResponseDTO(
                ventasVencidas.size(),
                idsUsuariosBloqueados.size()
        );
    }

    private void procesarVenta(
            VentaConcretada venta,
            Set<Long> idsUsuariosBloqueados
    ) {
        if (venta == null
                || venta.getEstado()
                != EstadoVenta.PENDIENTE_PAGO) {

            return;
        }

        Cliente cliente =
                venta.getComprador();

        if (cliente == null) {
            return;
        }

        venta.marcarIncumplida();

        /*
         * UsuarioRepository ya posee findByCliente().
         *
         * No hace falta agregar findByPersona().
         */
        Usuario usuario =
                usuarioRepository
                        .findByCliente(
                                cliente
                        )
                        .orElse(null);

        if (usuario == null) {
            return;
        }

        usuario.bloquear();

        usuarioRepository.save(
                usuario
        );

        if (usuario.getIdUsuario() != null) {
            idsUsuariosBloqueados.add(
                    usuario.getIdUsuario()
            );
        }
    }
}