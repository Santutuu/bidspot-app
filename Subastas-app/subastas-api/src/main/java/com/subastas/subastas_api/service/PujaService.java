package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.puja.EstadoPujaSubastaResponseDTO;
import com.subastas.subastas_api.DTO.puja.PujaActualizadaEventDTO;
import com.subastas.subastas_api.DTO.puja.PujaRequestDTO;
import com.subastas.subastas_api.DTO.puja.PujaResponseDTO;
import com.subastas.subastas_api.events.PujaActualizadaEvent;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.ItemCatalogoRepository;
import com.subastas.subastas_api.repository.ParticipacionSubastaRepository;
import com.subastas.subastas_api.repository.PujaRepository;
import com.subastas.subastas_api.repository.SubastaRepository;
import com.subastas.subastas_api.repository.TarjetaCreditoRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PujaService {

    private final ApplicationEventPublisher eventPublisher;
    private final SubastaRepository subastaRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final PujaRepository pujaRepository;
    private final ParticipacionSubastaRepository participacionRepository;
    private final TarjetaCreditoRepository tarjetaCreditoRepository;

    public PujaService(SubastaRepository subastaRepository,
                       ItemCatalogoRepository itemCatalogoRepository,
                       PujaRepository pujaRepository,
                       ParticipacionSubastaRepository participacionRepository,
                       ApplicationEventPublisher eventPublisher,
                       TarjetaCreditoRepository tarjetaCreditoRepository) {
        this.subastaRepository = subastaRepository;
        this.itemCatalogoRepository = itemCatalogoRepository;
        this.pujaRepository = pujaRepository;
        this.participacionRepository = participacionRepository;
        this.eventPublisher = eventPublisher;
        this.tarjetaCreditoRepository = tarjetaCreditoRepository;
    }

    public EstadoPujaSubastaResponseDTO obtenerEstadoPuja(Long idSubasta, Usuario usuario) {
        Subasta subasta = obtenerSubastaActiva(idSubasta);
        ItemCatalogo itemActual = obtenerItemActual(subasta);

        Float precioBase = itemActual.getPrecioBase();
        Float mejorOferta = itemActual.obtenerMejorOferta();

        Float incrementoMinimo = itemActual.calcularIncrementoMinimo();
        Float incrementoMaximo = itemActual.calcularIncrementoMaximo();

        boolean sinLimiteMaximo =
                subasta.getCategoriaMin() == CategoriaUsuario.ORO
                        || subasta.getCategoriaMin() == CategoriaUsuario.PLATINO;

        Float ofertaMinima = mejorOferta + incrementoMinimo;
        Float ofertaMaxima = sinLimiteMaximo
                ? null
                : mejorOferta + incrementoMaximo;

        Float miMejorOferta = null;
        boolean soyMejorPostor = false;

        if (usuario != null) {
            miMejorOferta = pujaRepository
                    .findTopByItemCatalogoAndUsuarioOrderByMontoDesc(itemActual, usuario)
                    .map(Puja::getMonto)
                    .orElse(null);

            Puja pujaActual = itemActual.getPujaActual();

            soyMejorPostor =
                    pujaActual != null
                            && pujaActual.getUsuario() != null
                            && pujaActual.getUsuario().getIdUsuario().equals(usuario.getIdUsuario());
        }

        return new EstadoPujaSubastaResponseDTO(
                subasta.getIdSubasta(),
                itemActual.getIdItemCatalogo(),
                precioBase,
                mejorOferta,
                incrementoMinimo,
                incrementoMaximo,
                ofertaMinima,
                ofertaMaxima,
                subasta.getMoneda() != null ? subasta.getMoneda().name() : null,
                miMejorOferta,
                soyMejorPostor
        );
    }

    @Transactional
    public PujaResponseDTO realizarPuja(Long idSubasta,
                                        Usuario usuario,
                                        PujaRequestDTO request) {

        Subasta subasta = obtenerSubastaActiva(idSubasta);

        /*
         * LOCK PESIMISTA.
         *
         * PostgreSQL bloquea la fila del ItemCatalogo hasta que
         * esta transacción termina (COMMIT o ROLLBACK).
         *
         * Si llega otra puja concurrente sobre el mismo lote,
         * deberá esperar.
         */
        ItemCatalogo itemActual = itemCatalogoRepository
                .findItemActualBySubastaAndEstadoForUpdate(
                        subasta,
                        EstadoItemCatalogo.EN_REMATE
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "La subasta no tiene un lote en remate"
                ));

        /*
         * IMPORTANTE:
         *
         * Las validaciones dependientes del estado actual del lote
         * ocurren después de adquirir el lock.
         */

        validarMonto(
                subasta,
                itemActual,
                request
        );

        validarPuedeParticipar(
                subasta,
                usuario,
                request.getMonto()
        );

        validarParticipacionUnica(
                subasta,
                usuario
        );

        /*
         * La puja actualmente ganadora deja de serlo.
         */

        Puja pujaAnterior = itemActual.getPujaActual();

        if (pujaAnterior != null) {
            pujaAnterior.marcarSuperada();
            pujaRepository.save(pujaAnterior);
        }

        /*
         * Creamos la nueva puja.
         */

        Puja nuevaPuja = new Puja(
                usuario,
                subasta,
                itemActual,
                request.getMonto()
        );

        Puja pujaGuardada =
                pujaRepository.save(nuevaPuja);

        /*
         * Actualizamos la referencia a la mejor puja del lote.
         */

        itemActual.recibirPuja(pujaGuardada);

        itemCatalogoRepository.save(itemActual);

        /*
         * La participación se crea automáticamente
         * con la primera puja del usuario.
         */

        registrarParticipacionSiNoExiste(
                subasta,
                usuario
        );

        /*
         * Publicamos el evento dentro de la transacción.
         *
         * PujaWebSocketListener utiliza:
         *
         * @TransactionalEventListener(AFTER_COMMIT)
         *
         * Por lo tanto, el WebSocket solamente será enviado
         * si PostgreSQL confirma correctamente la transacción.
         */

        PujaActualizadaEventDTO eventoDTO =
                new PujaActualizadaEventDTO(
                        subasta.getIdSubasta(),
                        itemActual.getIdItemCatalogo(),
                        pujaGuardada.getMonto(),
                        subasta.getMoneda() != null
                                ? subasta.getMoneda().name()
                                : null,
                        pujaGuardada.getFechaHora()
                );

        eventPublisher.publishEvent(
                new PujaActualizadaEvent(eventoDTO)
        );

        return toResponseDTO(
                pujaGuardada,
                subasta,
                true
        );
    }

    private Subasta obtenerSubastaActiva(Long idSubasta) {

        Subasta subasta = subastaRepository
                .findById(idSubasta)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una subasta con id " + idSubasta
                ));

        if (subasta.getEstadoSubasta() != EstadoSubasta.ACTIVA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La subasta no está activa"
            );
        }

        return subasta;
    }

    private ItemCatalogo obtenerItemActual(Subasta subasta) {

        return itemCatalogoRepository
                .findItemActualBySubastaAndEstado(
                        subasta,
                        EstadoItemCatalogo.EN_REMATE
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "La subasta no tiene un lote en remate"
                ));
    }

    private void validarPuedeParticipar(Subasta subasta,
                                        Usuario usuario,
                                        Float monto) {

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesión para pujar"
            );
        }

        if (usuario.getEstado() != EstadoUsuario.VALIDADO) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no está validado"
            );
        }

        if (usuario.getCategoria() == null
                || usuario.getCategoria().ordinal()
                < subasta.getCategoriaMin().ordinal()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La categoría del usuario no permite participar en esta subasta"
            );
        }

        if (usuario.getCuenta() == null
                || tarjetaCreditoRepository.countByUsuario(usuario) == 0) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Debe tener una cuenta de cobro y una tarjeta de crÃ©dito cargadas para pujar"
            );
        }

        validarGarantiaDisponible(
                usuario,
                subasta,
                monto
        );
    }

    private void validarGarantiaDisponible(Usuario usuario,
                                           Subasta subasta,
                                           Float monto) {

        boolean tieneGarantia =
                usuario.getMediosDePago()
                        .stream()
                        .filter(medio ->
                                medio.getMoneda()
                                        .esMismaMoneda(subasta.getMoneda())
                        )
                        .anyMatch(medio ->
                                puedeGarantizar(medio, monto)
                        );

        if (!tieneGarantia) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No posee un medio de pago en "
                            + subasta.getMoneda().name()
                            + " con garantía suficiente para realizar esta puja"
            );
        }
    }

    private boolean puedeGarantizar(MedioDePago medio,
                                    Float monto) {

        if (medio instanceof TarjetaCredito tarjeta) {
            return tarjeta.getLimiteCredito() >= monto;
        }

        if (medio instanceof Cheque cheque) {
            return cheque.getSaldo() >= monto;
        }

        return false;
    }

    private void validarParticipacionUnica(Subasta subasta,
                                           Usuario usuario) {

        participacionRepository
                .findByUsuarioAndEstado(
                        usuario,
                        EstadoParticipacionSubasta.ACTIVA
                )
                .ifPresent(participacion -> {

                    if (!participacion
                            .getSubasta()
                            .getIdSubasta()
                            .equals(subasta.getIdSubasta())) {

                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "No puede participar en más de una subasta al mismo tiempo"
                        );
                    }
                });
    }

    private void registrarParticipacionSiNoExiste(Subasta subasta,
                                                  Usuario usuario) {

        boolean yaParticipa =
                participacionRepository
                        .findByUsuarioAndEstado(
                                usuario,
                                EstadoParticipacionSubasta.ACTIVA
                        )
                        .isPresent();

        if (!yaParticipa) {

            ParticipacionSubasta participacion =
                    new ParticipacionSubasta(
                            usuario,
                            subasta
                    );

            participacionRepository.save(participacion);
        }
    }

    private void validarMonto(Subasta subasta,
                              ItemCatalogo itemActual,
                              PujaRequestDTO request) {

        if (request.getMonto() == null
                || request.getMonto() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El monto de la puja es obligatorio"
            );
        }

        Float mejorOferta =
                itemActual.obtenerMejorOferta();

        Float ofertaMinima =
                mejorOferta
                        + itemActual.calcularIncrementoMinimo();

        if (request.getMonto() < ofertaMinima) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La oferta debe ser al menos "
                            + ofertaMinima
            );
        }

        boolean sinLimiteMaximo =
                subasta.getCategoriaMin() == CategoriaUsuario.ORO
                        || subasta.getCategoriaMin() == CategoriaUsuario.PLATINO;

        if (!sinLimiteMaximo) {

            Float ofertaMaxima =
                    mejorOferta
                            + itemActual.calcularIncrementoMaximo();

            if (request.getMonto() > ofertaMaxima) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La oferta no puede superar "
                                + ofertaMaxima
                );
            }
        }
    }

    private PujaResponseDTO toResponseDTO(Puja puja,
                                          Subasta subasta,
                                          boolean mejorOferta) {

        return new PujaResponseDTO(
                puja.getIdPuja(),
                subasta.getIdSubasta(),
                puja.getItemCatalogo().getIdItemCatalogo(),
                puja.getMonto(),
                subasta.getMoneda() != null
                        ? subasta.getMoneda().name()
                        : null,
                puja.getEstado().name(),
                puja.getFechaHora(),
                mejorOferta
        );
    }
}
