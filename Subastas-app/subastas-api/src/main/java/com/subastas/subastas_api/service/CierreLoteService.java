package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.subasta.CierreLoteResponseDTO;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.*;
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
    private final EstadoItemCatalogoRepository estadoItemCatalogoRepository;
    private final EstadoSubastaRepository estadoSubastaRepository;
    private final UsuarioRepository usuarioRepository;

    public CierreLoteService(SubastaRepository subastaRepository,
                             ItemCatalogoRepository itemCatalogoRepository,
                             PujaRepository pujaRepository,
                             VentaConcretadaRepository ventaRepository,
                             EstadoItemCatalogoRepository estadoItemCatalogoRepository,
                             EstadoSubastaRepository estadoSubastaRepository,
                             UsuarioRepository usuarioRepository) {
        this.subastaRepository = subastaRepository;
        this.itemCatalogoRepository = itemCatalogoRepository;
        this.pujaRepository = pujaRepository;
        this.ventaRepository = ventaRepository;
        this.estadoItemCatalogoRepository = estadoItemCatalogoRepository;
        this.estadoSubastaRepository = estadoSubastaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public CierreLoteResponseDTO cerrarLote(Long idSubasta, Long idItemCatalogo) {
        Subasta subasta = obtenerSubasta(idSubasta);
        ItemCatalogo lote = obtenerLote(idItemCatalogo);

        validarLotePerteneceASubasta(lote, subasta);

        Puja pujaGanadora = pujaRepository
                .findTopByItemCatalogoOrderByMontoDesc(lote)
                .orElse(null);

        VentaConcretada venta = null;
        Usuario comprador = null;

        if (pujaGanadora != null) {
            pujaGanadora.marcarGanadora();
            pujaRepository.save(pujaGanadora);

            comprador = obtenerUsuarioComprador(pujaGanadora);

            Float montoPuja = pujaGanadora.getMonto();
            Float comision = calcularComision(lote, montoPuja);
            Float costoEnvioInicial = 0f;
            Float total = montoPuja + comision + costoEnvioInicial;

            Usuario compradorFinal = comprador;

            venta = ventaRepository.findByItemCatalogo(lote)
                    .orElseGet(() -> new VentaConcretada(
                            compradorFinal,
                            lote,
                            pujaGanadora,
                            montoPuja,
                            comision,
                            costoEnvioInicial,
                            total,
                            subasta.getUbicacion()
                    ));

            ventaRepository.save(venta);

            cambiarEstadoLote(lote, EstadoItemCatalogo.VENDIDO);
        } else {
            cambiarEstadoLote(lote, EstadoItemCatalogo.SIN_OFERTAS);
        }

        itemCatalogoRepository.save(lote);

        Long idProximoLote = abrirProximoLoteOSubastaFinalizada(lote, subasta);

        return new CierreLoteResponseDTO(
                subasta.getIdSubasta(),
                lote.getIdItemCatalogo(),
                venta != null ? venta.getIdVenta() : null,
                comprador != null ? comprador.getIdUsuario() : null,
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

        pujaRepository.findTopByItemCatalogoOrderByMontoDesc(lote)
                .ifPresent(puja -> {
                    puja.marcarRegistrada();
                    pujaRepository.save(puja);
                });

        itemCatalogoRepository.findByCatalogoAndEstado(
                        lote.getCatalogo(),
                        EstadoItemCatalogo.EN_REMATE
                )
                .forEach(l -> {
                    if (!l.getIdItemCatalogo().equals(lote.getIdItemCatalogo())) {
                        cambiarEstadoLote(l, EstadoItemCatalogo.PENDIENTE);
                        itemCatalogoRepository.save(l);
                    }
                });

        cambiarEstadoLote(lote, EstadoItemCatalogo.EN_REMATE);
        itemCatalogoRepository.save(lote);

        cambiarEstadoSubasta(subasta, EstadoSubasta.ACTIVA);
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

    private Long abrirProximoLoteOSubastaFinalizada(ItemCatalogo loteCerrado, Subasta subasta) {
        Optional<ItemCatalogo> proximoLote = itemCatalogoRepository
                .findFirstByCatalogoAndEstadoOrderByIdItemCatalogoAsc(
                        loteCerrado.getCatalogo(),
                        EstadoItemCatalogo.PENDIENTE
                );

        if (proximoLote.isPresent()) {
            ItemCatalogo siguiente = proximoLote.get();
            cambiarEstadoLote(siguiente, EstadoItemCatalogo.EN_REMATE);
            itemCatalogoRepository.save(siguiente);
            return siguiente.getIdItemCatalogo();
        }

        cambiarEstadoSubasta(subasta, EstadoSubasta.FINALIZADA);
        subastaRepository.save(subasta);

        return null;
    }

    private Usuario obtenerUsuarioComprador(Puja pujaGanadora) {
        if (pujaGanadora.getAsistente() == null
                || pujaGanadora.getAsistente().getCliente() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La puja ganadora no tiene cliente asociado"
            );
        }

        return usuarioRepository
                .findByClienteLegacy(pujaGanadora.getAsistente().getCliente())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No existe usuario vinculado al cliente ganador"
                ));
    }

    private void cambiarEstadoLote(ItemCatalogo lote, EstadoItemCatalogo estado) {
        EstadoItemCatalogoEntity estadoEntity = estadoItemCatalogoRepository
                .findByNombre(estado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No existe el estado de lote " + estado
                ));

        lote.setEstadoEntity(estadoEntity);
    }

    private void cambiarEstadoSubasta(Subasta subasta, EstadoSubasta estado) {
        EstadoSubastaEntity estadoEntity = estadoSubastaRepository
                .findByNombre(estado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No existe el estado de subasta " + estado
                ));

        subasta.setEstado(estadoEntity);
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