package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.mensajeria.UltimaAdjudicacionResponseDTO;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.repository.VentaConcretadaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MensajeriaService {

    private final UsuarioRepository
            usuarioRepository;

    private final VentaConcretadaRepository
            ventaRepository;

    public MensajeriaService(
            UsuarioRepository usuarioRepository,
            VentaConcretadaRepository ventaRepository
    ) {
        this.usuarioRepository =
                usuarioRepository;

        this.ventaRepository =
                ventaRepository;
    }

    @Transactional(readOnly = true)
    public UltimaAdjudicacionResponseDTO
    obtenerUltimaAdjudicacion(
            String mail
    ) {
        Usuario usuario =
                usuarioRepository
                        .findByMail(
                                mail.trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Usuario no autenticado"
                                )
                        );

        Cliente cliente =
                usuario.getCliente();

        if (cliente == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario no tiene cliente asociado"
            );
        }

        VentaConcretada venta =
                ventaRepository
                        .findTopByCompradorOrderByFechaVentaDesc(
                                cliente
                        )
                        .orElse(null);

        if (venta == null) {
            return new UltimaAdjudicacionResponseDTO(
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        ItemCatalogo itemCatalogo =
                venta.getItemCatalogo();

        Item item =
                itemCatalogo.getItem();

        Subasta subasta =
                itemCatalogo
                        .getCatalogo()
                        .getSubasta();

        return new UltimaAdjudicacionResponseDTO(
                true,
                venta.getIdVenta(),
                itemCatalogo.getIdItemCatalogo(),
                item.getTitulo(),
                item.getPrimeraImagen(),
                venta.getMontoPuja(),
                venta.getComision(),
                venta.getCostoEnvio(),
                venta.getTotal(),
                subasta.getMoneda() != null
                        ? subasta.getMoneda().name()
                        : null,
                venta.getEstado().name(),
                venta.getFechaVenta(),
                venta.getFechaLimitePago()
        );
    }
}