package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.puja.EstadoPujaSubastaResponseDTO;
import com.subastas.subastas_api.DTO.puja.PujaActualizadaEventDTO;
import com.subastas.subastas_api.DTO.puja.PujaRequestDTO;
import com.subastas.subastas_api.DTO.puja.PujaResponseDTO;
import com.subastas.subastas_api.events.PujaActualizadaEvent;
import com.subastas.subastas_api.model.Asistente;
import com.subastas.subastas_api.model.CategoriaUsuario;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.EstadoItemCatalogo;
import com.subastas.subastas_api.model.EstadoSubasta;
import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.Puja;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.AsistenteRepository;
import com.subastas.subastas_api.repository.ItemCatalogoRepository;
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
    private final TarjetaCreditoRepository tarjetaCreditoRepository;
    private final AsistenteRepository asistenteRepository;
    private final DisponibilidadPagoService disponibilidadPagoService;

    public PujaService(
            SubastaRepository subastaRepository,
            ItemCatalogoRepository itemCatalogoRepository,
            PujaRepository pujaRepository,
            ApplicationEventPublisher eventPublisher,
            TarjetaCreditoRepository tarjetaCreditoRepository,
            AsistenteRepository asistenteRepository,
            DisponibilidadPagoService disponibilidadPagoService
    ) {
        this.subastaRepository =
                subastaRepository;

        this.itemCatalogoRepository =
                itemCatalogoRepository;

        this.pujaRepository =
                pujaRepository;

        this.eventPublisher =
                eventPublisher;

        this.tarjetaCreditoRepository =
                tarjetaCreditoRepository;

        this.asistenteRepository =
                asistenteRepository;

        this.disponibilidadPagoService =
                disponibilidadPagoService;
    }

    @Transactional(readOnly = true)
    public EstadoPujaSubastaResponseDTO obtenerEstadoPuja(
            Long idSubasta,
            Usuario usuario
    ) {
        Subasta subasta =
                obtenerSubastaActiva(idSubasta);

        ItemCatalogo itemActual =
                obtenerItemActual(subasta);

        Puja pujaActual =
                pujaRepository
                        .findTopByItemCatalogoOrderByMontoDesc(
                                itemActual
                        )
                        .orElse(null);

        Float mejorOferta =
                pujaActual != null
                        ? pujaActual.getMonto()
                        : itemActual.getPrecioBase();

        Float incrementoMinimo =
                itemActual.calcularIncrementoMinimo();

        Float incrementoMaximo =
                itemActual.calcularIncrementoMaximo();

        boolean sinLimitesDePuja =
                subastaSinLimitesDePuja(subasta);

        Float ofertaMinima =
                sinLimitesDePuja
                        ? null
                        : mejorOferta
                        + incrementoMinimo;

        Float ofertaMaxima =
                sinLimitesDePuja
                        ? null
                        : mejorOferta
                        + incrementoMaximo;

        Float miMejorOferta = null;
        boolean soyMejorPostor = false;

        if (usuario != null
                && usuario.getCliente() != null) {

            Cliente clienteUsuario =
                    usuario.getCliente();

            miMejorOferta =
                    pujaRepository
                            .findTopByItemCatalogoAndClienteOrderByMontoDesc(
                                    itemActual,
                                    clienteUsuario
                            )
                            .map(Puja::getMonto)
                            .orElse(null);

            soyMejorPostor =
                    esPujaDelUsuario(
                            pujaActual,
                            usuario
                    );
        }

        return new EstadoPujaSubastaResponseDTO(
                subasta.getIdSubasta(),
                itemActual.getIdItemCatalogo(),
                itemActual.getPrecioBase(),
                mejorOferta,
                incrementoMinimo,
                incrementoMaximo,
                ofertaMinima,
                ofertaMaxima,
                subasta.getMoneda() != null
                        ? subasta
                        .getMoneda()
                        .name()
                        : null,
                miMejorOferta,
                soyMejorPostor
        );
    }

    @Transactional
    public PujaResponseDTO realizarPuja(
            Long idSubasta,
            Usuario usuario,
            PujaRequestDTO request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de la puja son obligatorios"
            );
        }

        Subasta subasta =
                obtenerSubastaActiva(idSubasta);

        ItemCatalogo itemActual =
                itemCatalogoRepository
                        .findItemActualBySubastaAndEstadoForUpdate(
                                subasta,
                                EstadoItemCatalogo.EN_REMATE
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "La subasta no tiene un lote en remate"
                                )
                        );

        validarPuedeParticipar(
                subasta,
                usuario,
                request.getMonto()
        );

        validarMonto(
                subasta,
                itemActual,
                usuario,
                request
        );

        Asistente asistente =
                obtenerOCrearAsistente(
                        subasta,
                        usuario
                );

        Puja pujaAnterior =
                pujaRepository
                        .findTopByItemCatalogoOrderByMontoDesc(
                                itemActual
                        )
                        .orElse(null);

        if (pujaAnterior != null) {
            pujaAnterior.marcarSuperada();
            pujaRepository.save(pujaAnterior);
        }

        Puja nuevaPuja =
                new Puja(
                        asistente,
                        itemActual,
                        request.getMonto()
                );

        Puja pujaGuardada =
                pujaRepository.save(nuevaPuja);

        itemActual.setPujaActual(pujaGuardada);

        itemCatalogoRepository.save(itemActual);

        PujaActualizadaEventDTO eventoDTO =
                new PujaActualizadaEventDTO(
                        subasta.getIdSubasta(),
                        itemActual.getIdItemCatalogo(),
                        pujaGuardada.getMonto(),
                        subasta.getMoneda() != null
                                ? subasta
                                .getMoneda()
                                .name()
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

    private Subasta obtenerSubastaActiva(
            Long idSubasta
    ) {
        Subasta subasta =
                subastaRepository
                        .findById(idSubasta)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe una subasta con id "
                                                + idSubasta
                                )
                        );

        if (subasta.getEstadoSubasta()
                != EstadoSubasta.ACTIVA) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La subasta no está activa"
            );
        }

        return subasta;
    }

    private ItemCatalogo obtenerItemActual(
            Subasta subasta
    ) {
        return itemCatalogoRepository
                .findItemActualBySubastaAndEstado(
                        subasta,
                        EstadoItemCatalogo.EN_REMATE
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "La subasta no tiene un lote en remate"
                        )
                );
    }

    private Asistente obtenerOCrearAsistente(
            Subasta subasta,
            Usuario usuario
    ) {
        Cliente cliente =
                obtenerCliente(usuario);

        return asistenteRepository
                .findByClienteAndSubasta(
                        cliente,
                        subasta
                )
                .orElseGet(() -> {

                    Integer numeroPostor =
                            Math.toIntExact(
                                    cliente.getIdentificador()
                            );

                    Asistente asistente =
                            new Asistente(
                                    numeroPostor,
                                    cliente,
                                    subasta
                            );

                    return asistenteRepository.save(
                            asistente
                    );
                });
    }

    private void validarPuedeParticipar(
            Subasta subasta,
            Usuario usuario,
            Float monto
    ) {
        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesión para pujar"
            );
        }

        if (usuario.estaBloqueado()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La cuenta se encuentra bloqueada"
            );
        }

        if (!usuario.estaValidadoComoCliente()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no está validado como cliente"
            );
        }

        Cliente cliente =
                obtenerCliente(usuario);

        if (!cliente.estaAdmitido()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El cliente no está admitido"
            );
        }

        /*
         * La suspensión comercial no bloquea el inicio de sesión.
         *
         * El usuario puede seguir entrando a Mis compras y pagar
         * la multa, pero no puede registrar nuevas pujas.
         */
        if (cliente.isSuspendidoParaPujar()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puede realizar nuevas pujas porque posee una multa pendiente"
            );
        }

        CategoriaUsuario categoria =
                cliente.getCategoria();

        if (categoria == null
                || subasta.getCategoriaMin() == null
                || categoria.ordinal()
                < subasta
                .getCategoriaMin()
                .ordinal()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La categoría del cliente no permite participar en esta subasta"
            );
        }

        if (cliente.getCuenta() == null
                || tarjetaCreditoRepository
                .countByCliente(cliente) == 0) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Debe tener una cuenta de cobro y una tarjeta de crédito cargadas para pujar"
            );
        }

        validarGarantiaDisponible(
                cliente,
                subasta,
                monto
        );
    }

    private Cliente obtenerCliente(
            Usuario usuario
    ) {
        if (usuario == null
                || usuario.getCliente() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario no tiene un perfil de cliente asociado"
            );
        }

        return usuario.getCliente();
    }

    private void validarGarantiaDisponible(
            Cliente cliente,
            Subasta subasta,
            Float monto
    ) {
        if (monto == null || monto <= 0f) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El monto de la puja es obligatorio"
            );
        }

        if (subasta.getMoneda() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La subasta no tiene una moneda configurada"
            );
        }

        boolean tieneGarantia =
                disponibilidadPagoService
                        .tieneRespaldoSuficiente(
                                cliente,
                                subasta.getMoneda(),
                                monto
                        );

        if (!tieneGarantia) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No posee un medio de pago en "
                            + subasta
                            .getMoneda()
                            .normalizada()
                            .name()
                            + " con garantía suficiente para realizar esta puja"
            );
        }
    }

    private void validarMonto(
            Subasta subasta,
            ItemCatalogo itemActual,
            Usuario usuario,
            PujaRequestDTO request
    ) {
        if (request.getMonto() == null
                || request.getMonto() <= 0f) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El monto de la puja es obligatorio"
            );
        }

        Puja pujaActual =
                pujaRepository
                        .findTopByItemCatalogoOrderByMontoDesc(
                                itemActual
                        )
                        .orElse(null);

        if (esPujaDelUsuario(
                pujaActual,
                usuario
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya sos el mejor postor. Esperá a que superen tu oferta para realizar otra puja en el mismo ítem"
            );
        }

        Float mejorOferta =
                pujaActual != null
                        ? pujaActual.getMonto()
                        : itemActual.getPrecioBase();

        Float ofertaMinima =
                mejorOferta
                        + itemActual
                        .calcularIncrementoMinimo();

        boolean sinLimitesDePuja =
                subastaSinLimitesDePuja(subasta);

        if (!sinLimitesDePuja && request.getMonto() < ofertaMinima) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La oferta debe ser al menos "
                            + ofertaMinima
            );
        }

        if (!sinLimitesDePuja) {
            Float ofertaMaxima =
                    mejorOferta
                            + itemActual
                            .calcularIncrementoMaximo();

            if (request.getMonto() > ofertaMaxima) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La oferta no puede superar "
                                + ofertaMaxima
                );
            }
        }
    }

    private boolean subastaSinLimitesDePuja(Subasta subasta) {
        return subasta.getCategoriaMin() == CategoriaUsuario.ORO
                || subasta.getCategoriaMin() == CategoriaUsuario.PLATINO;
    }

    private boolean esPujaDelUsuario(
            Puja puja,
            Usuario usuario
    ) {
        Cliente clienteUsuario =
                usuario != null
                        ? usuario.getCliente()
                        : null;

        if (puja == null
                || clienteUsuario == null
                || puja.getAsistente() == null
                || puja.getAsistente()
                .getCliente() == null) {

            return false;
        }

        return puja.getAsistente()
                .getCliente()
                .getIdentificador()
                .equals(
                        clienteUsuario
                                .getIdentificador()
                );
    }

    private PujaResponseDTO toResponseDTO(
            Puja puja,
            Subasta subasta,
            boolean mejorOferta
    ) {
        return new PujaResponseDTO(
                puja.getIdPuja(),
                subasta.getIdSubasta(),
                puja.getItemCatalogo()
                        .getIdItemCatalogo(),
                puja.getMonto(),
                subasta.getMoneda() != null
                        ? subasta
                        .getMoneda()
                        .name()
                        : null,
                puja.getEstado().name(),
                puja.getFechaHora(),
                mejorOferta
        );
    }
}
