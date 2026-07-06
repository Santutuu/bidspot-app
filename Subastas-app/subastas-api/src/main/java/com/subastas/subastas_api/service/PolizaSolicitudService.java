package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.poliza.PolizaSolicitudResponseDTO;
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
import org.springframework.web.server.ResponseStatusException;

@Service
public class PolizaSolicitudService {

    private final SolicitudPublicacionRepository solicitudRepository;
    private final ItemRepository itemRepository;
    private final UsuarioRepository usuarioRepository;

    public PolizaSolicitudService(SolicitudPublicacionRepository solicitudRepository,
                                  ItemRepository itemRepository,
                                  UsuarioRepository usuarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.itemRepository = itemRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public PolizaSolicitudResponseDTO obtenerPoliza(Long idSolicitud,
                                                    Authentication authentication) {
        Usuario usuario = obtenerUsuario(authentication);

        SolicitudPublicacion solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitud no encontrada"
                ));

        if (solicitud.getUsuario() == null ||
                !solicitud.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La solicitud no pertenece al usuario autenticado"
            );
        }

        Item item = itemRepository.findByIdSolicitudPublicacion(idSolicitud)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Todavía no existe un item asociado a esta solicitud"
                ));

        Poliza poliza = item.getPoliza();

        if (poliza == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Todavía no existe una póliza asociada al item"
            );
        }

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

    private Usuario obtenerUsuario(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        return usuarioRepository.findByMail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no autenticado"
                ));
    }
}