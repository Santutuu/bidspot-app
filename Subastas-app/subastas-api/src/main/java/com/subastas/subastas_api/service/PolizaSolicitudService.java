package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.poliza.AumentarPolizaRequestDTO;
import com.subastas.subastas_api.DTO.poliza.PolizaSolicitudResponseDTO;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.Item;
import com.subastas.subastas_api.model.Poliza;
import com.subastas.subastas_api.model.SolicitudPublicacion;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.ItemRepository;
import com.subastas.subastas_api.repository.SolicitudPublicacionRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PolizaSolicitudService {

    private final SolicitudPublicacionRepository solicitudRepository;
    private final ItemRepository itemRepository;
    private final UsuarioRepository usuarioRepository;

    public PolizaSolicitudService(
            SolicitudPublicacionRepository solicitudRepository,
            ItemRepository itemRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.solicitudRepository = solicitudRepository;
        this.itemRepository = itemRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public PolizaSolicitudResponseDTO obtenerPoliza(
            Long idSolicitud,
            Authentication authentication
    ) {
        Cliente cliente =
                obtenerCliente(authentication);

        SolicitudPublicacion solicitud =
                obtenerSolicitudDelCliente(
                        idSolicitud,
                        cliente
                );

        Item item =
                obtenerItemDeSolicitud(idSolicitud);

        Poliza poliza =
                obtenerPolizaDeItem(item);

        return toDTO(
                solicitud,
                item,
                poliza
        );
    }

    @Transactional
    public PolizaSolicitudResponseDTO aumentarPoliza(
            Long idSolicitud,
            AumentarPolizaRequestDTO request,
            Authentication authentication
    ) {
        Cliente cliente =
                obtenerCliente(authentication);

        SolicitudPublicacion solicitud =
                obtenerSolicitudDelCliente(
                        idSolicitud,
                        cliente
                );

        Item item =
                obtenerItemDeSolicitud(idSolicitud);

        Poliza poliza =
                obtenerPolizaDeItem(item);

        if (request == null
                || request.getNuevoMontoAsegurado() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nuevo monto asegurado es obligatorio"
            );
        }

        Float nuevoMonto =
                request.getNuevoMontoAsegurado();

        if (nuevoMonto <= poliza.getMontoAsegurado()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nuevo monto asegurado debe ser mayor al monto asegurado actual"
            );
        }

        poliza.solicitarAumento(nuevoMonto);

        return toDTO(
                solicitud,
                item,
                poliza
        );
    }

    private Cliente obtenerCliente(
            Authentication authentication
    ) {
        if (authentication == null
                || authentication.getName() == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        Usuario usuario = usuarioRepository
                .findByMail(
                        authentication.getName()
                                .trim()
                                .toLowerCase()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Usuario no autenticado"
                        )
                );

        if (usuario.estaBloqueado()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La cuenta se encuentra bloqueada"
            );
        }

        Cliente cliente =
                usuario.getCliente();

        if (cliente == null
                || !usuario.estaValidadoComoCliente()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no está validado como cliente"
            );
        }

        return cliente;
    }

    private SolicitudPublicacion obtenerSolicitudDelCliente(
            Long idSolicitud,
            Cliente cliente
    ) {
        return solicitudRepository
                .findByIdSolicitudAndCliente(
                        idSolicitud,
                        cliente
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Solicitud no encontrada para el cliente autenticado"
                        )
                );
    }

    private Item obtenerItemDeSolicitud(
            Long idSolicitud
    ) {
        return itemRepository
                .findByIdSolicitudPublicacion(idSolicitud)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Todavía no existe un item asociado a esta solicitud"
                        )
                );
    }

    private Poliza obtenerPolizaDeItem(
            Item item
    ) {
        if (item.getPoliza() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Todavía no existe una póliza asociada al item"
            );
        }

        return item.getPoliza();
    }

    private PolizaSolicitudResponseDTO toDTO(
            SolicitudPublicacion solicitud,
            Item item,
            Poliza poliza
    ) {
        return new PolizaSolicitudResponseDTO(
                solicitud.getIdSolicitud(),
                item.getIdItem(),
                item.getTitulo(),
                poliza.getIdPoliza(),
                poliza.getNroPoliza(),
                poliza.getMontoAsegurado(),
                poliza.getPremio(),
                poliza.getPrecioBase(),
                poliza.getTasaSeguro(),
                poliza.getCompania(),
                poliza.getEstado()
        );
    }
}