package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.subasta.CierreLoteResponseDTO;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class CierreLoteService {

    /*
     * Tarifa fija provisoria para la entrega a domicilio.
     *
     * Más adelante puede reemplazarse por un servicio de logística
     * sin modificar VentaConcretada.
     */
    private static final Float TARIFA_FIJA_ENVIO = 999f;

    private static final BigDecimal PORCENTAJE_MULTA =
            new BigDecimal("0.10");

    private final SubastaRepository subastaRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final PujaRepository pujaRepository;
    private final VentaConcretadaRepository ventaRepository;
    private final RegistroSubastaRepository registroSubastaRepository;
    private final EstadoItemCatalogoRepository estadoItemCatalogoRepository;
    private final EstadoSubastaRepository estadoSubastaRepository;
    private final PenalizacionRepository penalizacionRepository;
    private final DisponibilidadPagoService disponibilidadPagoService;

    public CierreLoteService(
            SubastaRepository subastaRepository,
            ItemCatalogoRepository itemCatalogoRepository,
            PujaRepository pujaRepository,
            VentaConcretadaRepository ventaRepository,
            RegistroSubastaRepository registroSubastaRepository,
            EstadoItemCatalogoRepository estadoItemCatalogoRepository,
            EstadoSubastaRepository estadoSubastaRepository,
            PenalizacionRepository penalizacionRepository,
            DisponibilidadPagoService disponibilidadPagoService
    ) {
        this.subastaRepository =
                subastaRepository;

        this.itemCatalogoRepository =
                itemCatalogoRepository;

        this.pujaRepository =
                pujaRepository;

        this.ventaRepository =
                ventaRepository;

        this.registroSubastaRepository =
                registroSubastaRepository;

        this.estadoItemCatalogoRepository =
                estadoItemCatalogoRepository;

        this.estadoSubastaRepository =
                estadoSubastaRepository;

        this.penalizacionRepository =
                penalizacionRepository;

        this.disponibilidadPagoService =
                disponibilidadPagoService;
    }

    @Transactional
    public CierreLoteResponseDTO cerrarLote(
            Long idSubasta,
            Long idItemCatalogo
    ) {
        Subasta subasta =
                obtenerSubasta(idSubasta);

        ItemCatalogo lote =
                obtenerLote(idItemCatalogo);

        validarLotePerteneceASubasta(
                lote,
                subasta
        );

        validarMonedaSubasta(subasta);

        Puja pujaGanadora =
                pujaRepository
                        .findTopByItemCatalogoOrderByMontoDesc(lote)
                        .orElse(null);

        VentaConcretada venta = null;
        Cliente comprador = null;

        if (pujaGanadora != null) {

            pujaGanadora.marcarGanadora();
            pujaRepository.save(pujaGanadora);

            lote.setPujaActual(pujaGanadora);

            comprador =
                    obtenerClienteComprador(pujaGanadora);

            Float montoPuja =
                    pujaGanadora.getMonto();

            Float comision =
                    calcularComision(
                            lote,
                            montoPuja
                    );

            String direccionPredeterminada =
                    obtenerDireccionComprador(comprador);

            Float costoEnvioInicial =
                    TARIFA_FIJA_ENVIO;

            Float total =
                    montoPuja
                            + comision
                            + costoEnvioInicial;

            registrarResultadoLegacy(
                    subasta,
                    lote,
                    comprador,
                    montoPuja,
                    comision
            );

            Optional<VentaConcretada> ventaExistente =
                    ventaRepository
                            .findByItemCatalogo(lote);

            if (ventaExistente.isPresent()) {
                venta = ventaExistente.get();
            } else {
                venta =
                        new VentaConcretada(
                                comprador,
                                lote,
                                pujaGanadora,
                                montoPuja,
                                comision,
                                costoEnvioInicial,
                                total,
                                subasta.getUbicacion()
                        );

                /*
                 * La compra queda configurada inicialmente para
                 * entrega al domicilio declarado por el comprador.
                 *
                 * El usuario podrá cambiarla después a retiro.
                 */
                venta.configurarEntregaDomicilio(
                        direccionPredeterminada,
                        costoEnvioInicial
                );

                venta =
                        ventaRepository.save(venta);
            }

            verificarRespaldoYAplicarMulta(
                    comprador,
                    venta,
                    subasta.getMoneda(),
                    montoPuja
            );

            cambiarEstadoLote(
                    lote,
                    EstadoItemCatalogo.VENDIDO
            );

        } else {
            lote.setPujaActual(null);

            cambiarEstadoLote(
                    lote,
                    EstadoItemCatalogo.SIN_OFERTAS
            );
        }

        itemCatalogoRepository.save(lote);

        Long idProximoLote =
                abrirProximoLoteOSubastaFinalizada(
                        lote,
                        subasta
                );

        return new CierreLoteResponseDTO(
                subasta.getIdSubasta(),
                lote.getIdItemCatalogo(),
                venta != null
                        ? venta.getIdVenta()
                        : null,
                comprador != null
                        ? comprador.getIdentificador()
                        : null,
                lote.getEstado().name(),
                subasta.getEstadoSubasta().name(),
                idProximoLote
        );
    }

    @Transactional
    public CierreLoteResponseDTO reabrirLote(
            Long idSubasta,
            Long idItemCatalogo
    ) {
        Subasta subasta =
                obtenerSubasta(idSubasta);

        ItemCatalogo lote =
                obtenerLote(idItemCatalogo);

        validarLotePerteneceASubasta(
                lote,
                subasta
        );

        /*
         * Antes de eliminar la venta se eliminan las penalizaciones
         * que dependan de ella.
         *
         * Esto evita violaciones de FK y revierte correctamente
         * la suspensión generada por el cierre anterior.
         */
        ventaRepository
                .findByItemCatalogo(lote)
                .ifPresent(this::eliminarVentaYPenalizaciones);

        /*
         * El registro legacy representa una operación cerrada.
         * Al reabrir el lote se elimina para mantener consistencia.
         */
        registroSubastaRepository
                .findBySubastaAndProducto(
                        subasta,
                        lote.getItem()
                )
                .ifPresent(
                        registroSubastaRepository::delete
                );

        Puja pujaAnterior =
                pujaRepository
                        .findTopByItemCatalogoOrderByMontoDesc(lote)
                        .orElse(null);

        if (pujaAnterior != null) {
            pujaAnterior.marcarRegistrada();
            pujaRepository.save(pujaAnterior);
        }

        lote.setPujaActual(pujaAnterior);

        itemCatalogoRepository
                .findByCatalogoAndEstado(
                        lote.getCatalogo(),
                        EstadoItemCatalogo.EN_REMATE
                )
                .forEach(loteActual -> {

                    if (!loteActual
                            .getIdItemCatalogo()
                            .equals(
                                    lote.getIdItemCatalogo()
                            )) {

                        cambiarEstadoLote(
                                loteActual,
                                EstadoItemCatalogo.PENDIENTE
                        );

                        itemCatalogoRepository.save(
                                loteActual
                        );
                    }
                });

        cambiarEstadoLote(
                lote,
                EstadoItemCatalogo.EN_REMATE
        );

        itemCatalogoRepository.save(lote);

        cambiarEstadoSubasta(
                subasta,
                EstadoSubasta.ACTIVA
        );

        subastaRepository.save(subasta);

        return new CierreLoteResponseDTO(
                subasta.getIdSubasta(),
                lote.getIdItemCatalogo(),
                null,
                null,
                lote.getEstado().name(),
                subasta.getEstadoSubasta().name(),
                lote.getIdItemCatalogo()
        );
    }

    private void verificarRespaldoYAplicarMulta(
            Cliente comprador,
            VentaConcretada venta,
            Moneda monedaSubasta,
            Float montoPuja
    ) {
        boolean tieneRespaldo =
                disponibilidadPagoService
                        .tieneRespaldoSuficiente(
                                comprador,
                                monedaSubasta,
                                montoPuja
                        );

        if (tieneRespaldo) {
            return;
        }

        boolean multaYaExiste =
                penalizacionRepository
                        .existsByVentaAndTipo(
                                venta,
                                TipoPenalizacion.FALTA_FONDOS
                        );

        if (!multaYaExiste) {
            BigDecimal importeMulta =
                    calcularMulta(montoPuja);

            Penalizacion penalizacion =
                    new Penalizacion(
                            comprador,
                            venta,
                            importeMulta,
                            monedaSubasta.normalizada(),
                            TipoPenalizacion.FALTA_FONDOS
                    );

            penalizacionRepository.save(penalizacion);
        }

        comprador.suspenderParaPujar();

        /*
         * No se necesita save explícito del Cliente:
         * se encuentra administrado por JPA dentro de esta transacción
         * y Hibernate persistirá el cambio mediante dirty checking.
         */
    }

    private BigDecimal calcularMulta(
            Float montoPuja
    ) {
        if (montoPuja == null || montoPuja <= 0f) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede calcular la multa porque el monto de la puja no es válido"
            );
        }

        return BigDecimal
                .valueOf(montoPuja.doubleValue())
                .multiply(PORCENTAJE_MULTA)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private void eliminarVentaYPenalizaciones(
            VentaConcretada venta
    ) {
        Cliente comprador =
                venta.getComprador();

        List<Penalizacion> penalizaciones =
                penalizacionRepository
                        .findByVenta(venta);

        if (!penalizaciones.isEmpty()) {
            penalizacionRepository
                    .deleteAll(penalizaciones);

            /*
             * Ejecutamos los DELETE antes de consultar si quedan
             * otras penalizaciones pendientes para el mismo cliente.
             */
            penalizacionRepository.flush();
        }

        ventaRepository.delete(venta);
        ventaRepository.flush();

        boolean conservaPenalizacionesPendientes =
                penalizacionRepository
                        .existsByClienteAndEstado(
                                comprador,
                                EstadoPenalizacion.PENDIENTE
                        );

        if (!conservaPenalizacionesPendientes) {
            comprador.habilitarParaPujar();
        }
    }

    private String obtenerDireccionComprador(
            Cliente comprador
    ) {
        if (comprador.getPersona() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El comprador no tiene una persona asociada"
            );
        }

        String direccion =
                comprador
                        .getPersona()
                        .getDomicilio().toString();

        if (direccion == null
                || direccion.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El comprador no tiene una dirección declarada"
            );
        }

        return direccion.trim();
    }

    private void registrarResultadoLegacy(
            Subasta subasta,
            ItemCatalogo lote,
            Cliente comprador,
            Float montoPuja,
            Float comision
    ) {
        Item producto =
                lote.getItem();

        if (producto == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El lote no tiene producto asociado"
            );
        }

        Duenio duenio =
                producto.getDuenio();

        if (duenio == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El producto no tiene dueño legacy asociado"
            );
        }

        Optional<RegistroSubasta> existente =
                registroSubastaRepository
                        .findBySubastaAndProducto(
                                subasta,
                                producto
                        );

        if (existente.isPresent()) {
            return;
        }

        RegistroSubasta registro =
                new RegistroSubasta(
                        subasta,
                        duenio,
                        producto,
                        comprador,
                        BigDecimal.valueOf(
                                montoPuja.doubleValue()
                        ),
                        BigDecimal.valueOf(
                                comision.doubleValue()
                        )
                );

        registroSubastaRepository.save(registro);
    }

    private Cliente obtenerClienteComprador(
            Puja pujaGanadora
    ) {
        if (pujaGanadora.getAsistente() == null
                || pujaGanadora
                .getAsistente()
                .getCliente() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La puja ganadora no tiene cliente asociado"
            );
        }

        return pujaGanadora
                .getAsistente()
                .getCliente();
    }

    private Long abrirProximoLoteOSubastaFinalizada(
            ItemCatalogo loteCerrado,
            Subasta subasta
    ) {
        if (loteCerrado.getCatalogo() == null
                || loteCerrado
                .getCatalogo()
                .getIdCatalogo() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El lote cerrado no tiene un catálogo asociado"
            );
        }

        Optional<ItemCatalogo> proximoLote =
                itemCatalogoRepository
                        .findFirstByCatalogoIdAndEstado(
                                loteCerrado
                                        .getCatalogo()
                                        .getIdCatalogo(),
                                EstadoItemCatalogo
                                        .PENDIENTE
                                        .name()
                        );

        if (proximoLote.isPresent()) {
            ItemCatalogo siguiente =
                    proximoLote.get();

            /*
             * Un próximo lote pendiente también debe empezar
             * sin pujas residuales.
             */
            siguiente.setPujaActual(null);

            itemCatalogoRepository.saveAndFlush(
                    siguiente
            );

            pujaRepository.deleteByItemCatalogo(
                    siguiente
            );

            pujaRepository.flush();

            cambiarEstadoLote(
                    siguiente,
                    EstadoItemCatalogo.EN_REMATE
            );

            itemCatalogoRepository.save(
                    siguiente
            );

            return siguiente.getIdItemCatalogo();
        }

        cambiarEstadoSubasta(
                subasta,
                EstadoSubasta.FINALIZADA
        );

        subastaRepository.save(
                subasta
        );

        return null;
    }

    private void cambiarEstadoLote(
            ItemCatalogo lote,
            EstadoItemCatalogo estado
    ) {
        EstadoItemCatalogoEntity estadoEntity =
                estadoItemCatalogoRepository
                        .findByNombre(estado)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "No existe el estado de lote "
                                                + estado
                                )
                        );

        lote.setEstadoEntity(estadoEntity);
    }

    private void cambiarEstadoSubasta(
            Subasta subasta,
            EstadoSubasta estado
    ) {
        EstadoSubastaEntity estadoEntity =
                estadoSubastaRepository
                        .findByNombre(estado)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "No existe el estado de subasta "
                                                + estado
                                )
                        );

        subasta.setEstado(estadoEntity);
    }

    private Float calcularComision(
            ItemCatalogo lote,
            Float montoPuja
    ) {
        if (montoPuja == null) {
            return 0f;
        }

        return montoPuja
                * lote.getComision()
                / 100f;
    }
    private void validarMonedaSubasta(
            Subasta subasta
    ) {
        if (subasta.getMoneda() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La subasta no tiene una moneda configurada"
            );
        }
    }

    private Subasta obtenerSubasta(
            Long idSubasta
    ) {
        return subastaRepository
                .findById(idSubasta)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe una subasta con id "
                                        + idSubasta
                        )
                );
    }

    private ItemCatalogo obtenerLote(
            Long idItemCatalogo
    ) {
        return itemCatalogoRepository
                .findById(idItemCatalogo)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe un lote con id "
                                        + idItemCatalogo
                        )
                );
    }

    private void validarLotePerteneceASubasta(
            ItemCatalogo lote,
            Subasta subasta
    ) {
        if (lote.getCatalogo() == null
                || lote.getCatalogo()
                .getSubasta() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El lote no está asociado a una subasta"
            );
        }

        Long idSubastaDelLote =
                lote.getCatalogo()
                        .getSubasta()
                        .getIdSubasta();

        if (!idSubastaDelLote.equals(
                subasta.getIdSubasta()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El lote no pertenece a la subasta indicada"
            );
        }
    }
}