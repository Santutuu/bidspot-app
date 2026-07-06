package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.subasta.CierreLoteResponseDTO;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.ItemCatalogoRepository;
import com.subastas.subastas_api.repository.PujaRepository;
import com.subastas.subastas_api.repository.SubastaRepository;
import com.subastas.subastas_api.repository.VentaConcretadaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class CierreLoteService {

    private final SubastaRepository subastaRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final PujaRepository pujaRepository;
    private final VentaConcretadaRepository ventaRepository;

    public CierreLoteService(SubastaRepository subastaRepository,
                             ItemCatalogoRepository itemCatalogoRepository,
                             PujaRepository pujaRepository,
                             VentaConcretadaRepository ventaRepository) {
        this.subastaRepository = subastaRepository;
        this.itemCatalogoRepository = itemCatalogoRepository;
        this.pujaRepository = pujaRepository;
        this.ventaRepository = ventaRepository;
    }

    @Transactional
    public CierreLoteResponseDTO cerrarLote(Long idSubasta, Long idItemCatalogo) {
        Subasta subasta = obtenerSubasta(idSubasta);
        ItemCatalogo lote = obtenerLote(idItemCatalogo);

        validarLotePerteneceASubasta(lote, subasta);

        if (lote.getEstado() == EstadoItemCatalogo.VENDIDO) {
            VentaConcretada ventaExistente = ventaRepository.findByItemCatalogo(lote).orElse(null);

            return new CierreLoteResponseDTO(
                    subasta.getIdSubasta(),
                    lote.getIdItemCatalogo(),
                    ventaExistente != null ? ventaExistente.getIdVenta() : null,
                    ventaExistente != null ? ventaExistente.getComprador().getIdUsuario() : null,
                    lote.getEstado().name(),
                    subasta.getEstadoSubasta().name(),
                    null
            );
        }

        Puja pujaGanadora = lote.getPujaActual();
        VentaConcretada venta = null;

        if (pujaGanadora != null) {
            pujaGanadora.marcarGanadora();
            pujaRepository.save(pujaGanadora);

            Float montoPuja = pujaGanadora.getMonto();
            Float comision = calcularComision(lote, montoPuja);
            Float costoEnvioInicial = 0f;
            Float total = montoPuja + comision + costoEnvioInicial;

            venta = ventaRepository.findByItemCatalogo(lote)
                    .orElseGet(() -> new VentaConcretada(
                            pujaGanadora.getUsuario(),
                            lote,
                            pujaGanadora,
                            montoPuja,
                            comision,
                            costoEnvioInicial,
                            total,
                            subasta.getUbicacion()
                    ));

            ventaRepository.save(venta);
        }

        lote.setEstado(EstadoItemCatalogo.VENDIDO);

        if (lote.getItem() != null) {
            lote.getItem().marcarComoVendido();
        }

        itemCatalogoRepository.save(lote);

        Long idProximoLote = abrirProximoLoteOSubastaFinalizada(lote, subasta);

        return new CierreLoteResponseDTO(
                subasta.getIdSubasta(),
                lote.getIdItemCatalogo(),
                venta != null ? venta.getIdVenta() : null,
                venta != null ? venta.getComprador().getIdUsuario() : null,
                lote.getEstado().name(),
                subasta.getEstadoSubasta().name(),
                idProximoLote
        );
    }

    @Transactional
    public CierreLoteResponseDTO reabrirLote(Long idSubasta, Long idItemCatalogo) {
        Subasta subasta = obtenerSubasta(idSubasta);
        ItemCatalogo lote = obtenerLote(idItemCatalogo);

        validarLotePerteneceASubasta(lote, subasta);

        ventaRepository.deleteByItemCatalogo(lote);

        if (lote.getPujaActual() != null) {
            lote.getPujaActual().marcarRegistrada();
            pujaRepository.save(lote.getPujaActual());
        }

        itemCatalogoRepository.findByCatalogoAndEstado(
                        lote.getCatalogo(),
                        EstadoItemCatalogo.EN_REMATE
                )
                .forEach(l -> {
                    if (!l.getIdItemCatalogo().equals(lote.getIdItemCatalogo())) {
                        l.setEstado(EstadoItemCatalogo.PENDIENTE);
                        itemCatalogoRepository.save(l);
                    }
                });

        lote.setEstado(EstadoItemCatalogo.EN_REMATE);

        if (lote.getItem() != null) {
            lote.getItem().setEstado(EstadoItem.EN_SUBASTA);
        }

        itemCatalogoRepository.save(lote);

        subasta.setEstadoSubasta(EstadoSubasta.ACTIVA);
        subastaRepository.save(subasta);

        return new CierreLoteResponseDTO(
                subasta.getIdSubasta(),
                lote.getIdItemCatalogo(),
                null,
                lote.getPujaActual() != null ? lote.getPujaActual().getUsuario().getIdUsuario() : null,
                lote.getEstado().name(),
                subasta.getEstadoSubasta().name(),
                lote.getIdItemCatalogo()
        );
    }

    private Long abrirProximoLoteOSubastaFinalizada(ItemCatalogo loteCerrado, Subasta subasta) {
        Optional<ItemCatalogo> proximoLote = itemCatalogoRepository
                .findFirstByCatalogoAndEstadoOrderByIdItemCatalogoAsc(
                        loteCerrado.getCatalogo(),
                        EstadoItemCatalogo.PENDIENTE
                );

        if (proximoLote.isPresent()) {
            ItemCatalogo siguiente = proximoLote.get();
            siguiente.setEstado(EstadoItemCatalogo.EN_REMATE);
            itemCatalogoRepository.save(siguiente);
            return siguiente.getIdItemCatalogo();
        }

        subasta.setEstadoSubasta(EstadoSubasta.FINALIZADA);
        subastaRepository.save(subasta);

        return null;
    }

    private Float calcularComision(ItemCatalogo lote, Float montoPuja) {
        if (montoPuja == null) {
            return 0f;
        }

        return montoPuja * lote.getComision() / 100f;
    }

    private Subasta obtenerSubasta(Long idSubasta) {
        return subastaRepository.findById(idSubasta)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una subasta con id " + idSubasta
                ));
    }

    private ItemCatalogo obtenerLote(Long idItemCatalogo) {
        return itemCatalogoRepository.findById(idItemCatalogo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un lote con id " + idItemCatalogo
                ));
    }

    private void validarLotePerteneceASubasta(ItemCatalogo lote, Subasta subasta) {
        Long idSubastaDelLote = lote.getCatalogo().getSubasta().getIdSubasta();

        if (!idSubastaDelLote.equals(subasta.getIdSubasta())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El lote no pertenece a la subasta indicada"
            );
        }
    }
}