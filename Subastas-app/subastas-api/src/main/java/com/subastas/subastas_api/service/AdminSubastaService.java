package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.subasta.ActivarSubastaRequestDTO;
import com.subastas.subastas_api.DTO.subasta.ActivarSubastaResponseDTO;
import com.subastas.subastas_api.model.Catalogo;
import com.subastas.subastas_api.model.EstadoItemCatalogo;
import com.subastas.subastas_api.model.EstadoItemCatalogoEntity;
import com.subastas.subastas_api.model.EstadoSubasta;
import com.subastas.subastas_api.model.EstadoSubastaEntity;
import com.subastas.subastas_api.model.Item;
import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.repository.EstadoItemCatalogoRepository;
import com.subastas.subastas_api.repository.EstadoSubastaRepository;
import com.subastas.subastas_api.repository.ItemCatalogoRepository;
import com.subastas.subastas_api.repository.PujaRepository;
import com.subastas.subastas_api.repository.SubastaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminSubastaService {

    private final SubastaRepository
            subastaRepository;

    private final ItemCatalogoRepository
            itemCatalogoRepository;

    private final EstadoSubastaRepository
            estadoSubastaRepository;

    private final EstadoItemCatalogoRepository
            estadoItemCatalogoRepository;

    private final PujaRepository
            pujaRepository;

    public AdminSubastaService(
            SubastaRepository subastaRepository,
            ItemCatalogoRepository itemCatalogoRepository,
            EstadoSubastaRepository estadoSubastaRepository,
            EstadoItemCatalogoRepository estadoItemCatalogoRepository,
            PujaRepository pujaRepository
    ) {
        this.subastaRepository =
                subastaRepository;

        this.itemCatalogoRepository =
                itemCatalogoRepository;

        this.estadoSubastaRepository =
                estadoSubastaRepository;

        this.estadoItemCatalogoRepository =
                estadoItemCatalogoRepository;

        this.pujaRepository =
                pujaRepository;
    }

    /**
     * Activa una subasta y deja exactamente un lote EN_REMATE.
     *
     * Si request.idItemCatalogo está presente, abre ese lote.
     *
     * Si no está presente, abre automáticamente el primer lote
     * PENDIENTE del catálogo, ordenado por identificador.
     */
    @Transactional
    public ActivarSubastaResponseDTO activarSubasta(
            Long idSubasta,
            ActivarSubastaRequestDTO request
    ) {
        Subasta subasta =
                obtenerSubasta(idSubasta);

        Catalogo catalogo =
                obtenerCatalogo(subasta);

        ItemCatalogo loteSeleccionado =
                seleccionarLote(
                        catalogo,
                        request
                );

        validarLotePerteneceAlCatalogo(
                loteSeleccionado,
                catalogo
        );

        validarLoteActivable(
                loteSeleccionado
        );

        /*
         * Un lote PENDIENTE debe comenzar desde cero.
         *
         * Esto elimina pujas residuales que puedan haber quedado
         * por pruebas anteriores.
         *
         * No se ejecuta si el lote ya estaba EN_REMATE,
         * para que una segunda llamada no borre pujas válidas.
         */
        if (loteSeleccionado.getEstado()
                == EstadoItemCatalogo.PENDIENTE) {

            limpiarPujasResiduales(
                    loteSeleccionado
            );
        }

        EstadoItemCatalogoEntity estadoPendiente =
                obtenerEstadoLote(
                        EstadoItemCatalogo.PENDIENTE
                );

        EstadoItemCatalogoEntity estadoEnRemate =
                obtenerEstadoLote(
                        EstadoItemCatalogo.EN_REMATE
                );

        EstadoSubastaEntity estadoActiva =
                obtenerEstadoSubasta(
                        EstadoSubasta.ACTIVA
                );

        /*
         * Garantiza que dentro del catálogo quede
         * exactamente un lote EN_REMATE.
         */
        List<ItemCatalogo> lotesActualmenteEnRemate =
                itemCatalogoRepository
                        .findByCatalogoAndEstado(
                                catalogo,
                                EstadoItemCatalogo.EN_REMATE
                        );

        for (ItemCatalogo loteActual
                : lotesActualmenteEnRemate) {

            if (loteActual
                    .getIdItemCatalogo()
                    .equals(
                            loteSeleccionado
                                    .getIdItemCatalogo()
                    )) {

                continue;
            }

            loteActual.setEstadoEntity(
                    estadoPendiente
            );

            itemCatalogoRepository.save(
                    loteActual
            );
        }

        loteSeleccionado.setEstadoEntity(
                estadoEnRemate
        );

        itemCatalogoRepository.save(
                loteSeleccionado
        );

        /*
         * Subasta.setEstado sincroniza:
         *
         * EstadoSubasta.ACTIVA
         *          ↓
         * estado legacy = "abierta"
         */
        subasta.setEstado(
                estadoActiva
        );

        subastaRepository.save(
                subasta
        );

        Item producto =
                loteSeleccionado.getItem();

        return new ActivarSubastaResponseDTO(
                subasta.getIdSubasta(),
                subasta.getEstadoSubasta().name(),
                catalogo.getIdCatalogo(),
                catalogo.getDescripcion(),
                loteSeleccionado
                        .getIdItemCatalogo(),
                producto != null
                        ? producto.getIdItem()
                        : null,
                producto != null
                        ? producto.getTitulo()
                        : null,
                loteSeleccionado
                        .getEstado()
                        .name(),
                "La subasta y el lote fueron activados correctamente"
        );
    }

    private Subasta obtenerSubasta(
            Long idSubasta
    ) {
        if (idSubasta == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El id de la subasta es obligatorio"
            );
        }

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

    private Catalogo obtenerCatalogo(
            Subasta subasta
    ) {
        Catalogo catalogo =
                subasta.getCatalogo();

        if (catalogo == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La subasta no tiene un catálogo asociado"
            );
        }

        return catalogo;
    }

    private ItemCatalogo seleccionarLote(
            Catalogo catalogo,
            ActivarSubastaRequestDTO request
    ) {
        Long idItemCatalogo =
                request != null
                        ? request.getIdItemCatalogo()
                        : null;

        /*
         * El lote específico es opcional.
         */
        if (idItemCatalogo != null) {
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

        /*
         * Si no se informa un lote, se selecciona automáticamente
         * el primero PENDIENTE del catálogo.
         */
        return itemCatalogoRepository
                .findFirstByCatalogoIdAndEstado(
                        catalogo.getIdCatalogo(),
                        EstadoItemCatalogo
                                .PENDIENTE
                                .name()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "La subasta no tiene lotes pendientes disponibles en el catálogo "
                                        + catalogo.getIdCatalogo()
                        )
                );
    }

    private void limpiarPujasResiduales(
            ItemCatalogo lote
    ) {
        /*
         * Primero se elimina la referencia desde itemscatalogo
         * hacia la posible puja actual.
         */
        lote.setPujaActual(null);

        itemCatalogoRepository.saveAndFlush(
                lote
        );

        /*
         * Después se eliminan las pujas del lote.
         */
        pujaRepository.deleteTodasPorItemCatalogo(
                lote
        );

        pujaRepository.flush();
    }

    private void validarLotePerteneceAlCatalogo(
            ItemCatalogo lote,
            Catalogo catalogo
    ) {
        if (lote.getCatalogo() == null
                || lote.getCatalogo()
                .getIdCatalogo() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El lote no tiene un catálogo asociado"
            );
        }

        boolean pertenece =
                lote.getCatalogo()
                        .getIdCatalogo()
                        .equals(
                                catalogo.getIdCatalogo()
                        );

        if (!pertenece) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El lote seleccionado no pertenece a la subasta indicada"
            );
        }
    }

    private void validarLoteActivable(
            ItemCatalogo lote
    ) {
        EstadoItemCatalogo estado =
                lote.getEstado();

        if (estado == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El lote no tiene un estado configurado"
            );
        }

        if (estado == EstadoItemCatalogo.VENDIDO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede abrir un lote vendido"
            );
        }

        if (estado == EstadoItemCatalogo.CANCELADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede abrir un lote cancelado"
            );
        }

        if (estado == EstadoItemCatalogo.SIN_OFERTAS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El lote está cerrado sin ofertas; debe reabrirse mediante el endpoint de reapertura"
            );
        }

        /*
         * Se admiten:
         *
         * PENDIENTE
         * EN_REMATE
         */
        if (estado != EstadoItemCatalogo.PENDIENTE
                && estado
                != EstadoItemCatalogo.EN_REMATE) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El lote no se encuentra en un estado activable"
            );
        }
    }

    private EstadoItemCatalogoEntity obtenerEstadoLote(
            EstadoItemCatalogo estado
    ) {
        return estadoItemCatalogoRepository
                .findByNombre(estado)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "No existe el estado de lote "
                                        + estado
                        )
                );
    }

    private EstadoSubastaEntity obtenerEstadoSubasta(
            EstadoSubasta estado
    ) {
        return estadoSubastaRepository
                .findByNombre(estado)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "No existe el estado de subasta "
                                        + estado
                        )
                );
    }
}