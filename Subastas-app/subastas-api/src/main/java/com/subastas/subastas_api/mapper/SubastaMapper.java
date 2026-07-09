package com.subastas.subastas_api.mapper;

import com.subastas.subastas_api.DTO.subasta.DetalleSubastaDTO;
import com.subastas.subastas_api.DTO.subasta.ItemActualDTO;
import com.subastas.subastas_api.DTO.subasta.ItemCatalogoPreviewDTO;
import com.subastas.subastas_api.DTO.subasta.SubastaHomeDTO;
import com.subastas.subastas_api.DTO.subasta.SubastaInfoDTO;
import com.subastas.subastas_api.model.EstadoItemCatalogo;
import com.subastas.subastas_api.model.EstadoSubasta;
import com.subastas.subastas_api.model.Item;
import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.PujaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class SubastaMapper {

    private final PujaRepository pujaRepository;

    public SubastaMapper(PujaRepository pujaRepository) {
        this.pujaRepository = pujaRepository;
    }

    public SubastaHomeDTO toHomeDTO(Subasta subasta) {
        ItemCatalogo itemVisible = obtenerItemVisibleParaCard(subasta);

        return new SubastaHomeDTO(
                subasta.getIdSubasta(),
                obtenerTituloCard(subasta),
                obtenerImagenPrincipal(itemVisible),
                obtenerPrecioMostrado(itemVisible),
                subasta.getMoneda() != null
                        ? subasta.getMoneda().toString()
                        : null,
                subasta.getEstadoSubasta(),
                subasta.getCategoriaMin(),
                subasta.getFechaInicio()
        );
    }

    public DetalleSubastaDTO toDetalleDTO(
            Subasta subasta,
            Usuario usuarioActual
    ) {
        SubastaInfoDTO subastaInfo =
                toSubastaInfoDTO(subasta, usuarioActual);

        /*
         * SUBASTA ACTIVA
         *
         * itemActual:
         * ItemCatalogo cuyo estado es EN_REMATE.
         *
         * proximosLotes:
         * ItemCatalogo cuyo estado es PENDIENTE.
         */
        if (subasta.getEstadoSubasta() == EstadoSubasta.ACTIVA) {

            ItemCatalogo itemActual =
                    obtenerItemActual(subasta);

            return new DetalleSubastaDTO(
                    subastaInfo,

                    itemActual != null
                            ? toItemActualDTO(itemActual)
                            : null,

                    List.of(),

                    obtenerProximosLotes(subasta)
            );
        }

        /*
         * SUBASTA PROGRAMADA
         *
         * Todavía no existe lote EN_REMATE.
         * Mostramos catálogo completo.
         */
        if (subasta.getEstadoSubasta() == EstadoSubasta.PROGRAMADA) {

            return new DetalleSubastaDTO(
                    subastaInfo,
                    null,
                    obtenerCatalogoCompleto(subasta),
                    List.of()
            );
        }

        /*
         * SUBASTA FINALIZADA
         */
        return new DetalleSubastaDTO(
                subastaInfo,
                null,
                List.of(),
                List.of()
        );
    }

    public ItemCatalogoPreviewDTO toItemPreviewDTO(
            ItemCatalogo itemCatalogo
    ) {
        Item item = itemCatalogo.getItem();

        return new ItemCatalogoPreviewDTO(
                itemCatalogo.getIdItemCatalogo(),
                obtenerNumeroLote(itemCatalogo),

                item != null
                        ? item.getTitulo()
                        : null,

                item != null
                        ? item.getPrimeraImagen()
                        : null,

                itemCatalogo.getPrecioBase()
        );
    }

    public ItemActualDTO toItemActualDTO(
            ItemCatalogo itemCatalogo
    ) {
        Item item = itemCatalogo.getItem();

        return new ItemActualDTO(
                itemCatalogo.getIdItemCatalogo(),

                obtenerNumeroLote(itemCatalogo),

                item != null
                        ? item.getTitulo()
                        : null,

                item != null
                        ? item.getDescripcion()
                        : null,

                item != null
                        ? obtenerImagenesItem(item)
                        : List.of(),

                itemCatalogo.getPrecioBase(),

                obtenerPrecioMostrado(itemCatalogo)
        );
    }

    private SubastaInfoDTO toSubastaInfoDTO(
            Subasta subasta,
            Usuario usuarioActual
    ) {
        return new SubastaInfoDTO(
                subasta.getIdSubasta(),
                obtenerTituloSubasta(subasta),
                subasta.getEstadoSubasta(),
                subasta.getCategoriaMin(),

                subasta.getMoneda() != null
                        ? subasta.getMoneda().toString()
                        : null,

                subasta.getFechaInicio(),
                subasta.getUbicacion(),
                obtenerNombreRematador(subasta),

                subasta.getEstadoSubasta() == EstadoSubasta.ACTIVA
                        ? subasta.getLinkVivo()
                        : null,

                estaGuardada(subasta, usuarioActual)
        );
    }

    /**
     * Determina qué lote debe mostrarse en las cards.
     *
     * ACTIVA:
     * muestra el ItemCatalogo EN_REMATE.
     *
     * PROGRAMADA:
     * muestra el primer lote.
     *
     * FINALIZADA:
     * actualmente devuelve el primer lote como fallback,
     * aunque las recomendadas normalmente solo incluyen ACTIVA.
     */
    private ItemCatalogo obtenerItemVisibleParaCard(
            Subasta subasta
    ) {
        if (subasta.getEstadoSubasta() == EstadoSubasta.ACTIVA) {

            ItemCatalogo itemActual =
                    obtenerItemActual(subasta);

            if (itemActual != null) {
                return itemActual;
            }
        }

        return obtenerPrimerItem(subasta);
    }

    /**
     * Devuelve el lote actualmente EN_REMATE.
     *
     * En una subasta consistente debería existir como máximo uno.
     *
     * Ordenamos por ID para tener comportamiento determinista
     * durante el MVP en caso de datos inconsistentes.
     */
    private ItemCatalogo obtenerItemActual(Subasta subasta) {
        if (subasta.getCatalogo() == null
                || subasta.getCatalogo().getItems() == null) {

            return null;
        }

        return subasta.getCatalogo()
                .getItems()
                .stream()
                .filter(item ->
                        item.getEstado() == EstadoItemCatalogo.EN_REMATE
                )
                .sorted(
                        Comparator.comparing(
                                ItemCatalogo::getIdItemCatalogo
                        )
                )
                .findFirst()
                .orElse(null);
    }

    /**
     * Devuelve el primer lote del catálogo ordenado por ID.
     *
     * Principalmente utilizado para subastas PROGRAMADAS.
     */
    private ItemCatalogo obtenerPrimerItem(
            Subasta subasta
    ) {
        if (subasta.getCatalogo() == null
                || subasta.getCatalogo().getItems() == null
                || subasta.getCatalogo().getItems().isEmpty()) {

            return null;
        }

        return subasta.getCatalogo()
                .getItems()
                .stream()
                .sorted(
                        Comparator.comparing(
                                ItemCatalogo::getIdItemCatalogo
                        )
                )
                .findFirst()
                .orElse(null);
    }

    /**
     * Devuelve todo el catálogo ordenado.
     */
    private List<ItemCatalogoPreviewDTO> obtenerCatalogoCompleto(
            Subasta subasta
    ) {
        if (subasta.getCatalogo() == null
                || subasta.getCatalogo().getItems() == null) {

            return List.of();
        }

        return subasta.getCatalogo()
                .getItems()
                .stream()
                .sorted(
                        Comparator.comparing(
                                ItemCatalogo::getIdItemCatalogo
                        )
                )
                .map(this::toItemPreviewDTO)
                .toList();
    }

    /**
     * Devuelve únicamente los lotes pendientes,
     * ordenados por ID.
     */
    private List<ItemCatalogoPreviewDTO> obtenerProximosLotes(
            Subasta subasta
    ) {
        if (subasta.getCatalogo() == null
                || subasta.getCatalogo().getItems() == null) {

            return List.of();
        }

        return subasta.getCatalogo()
                .getItems()
                .stream()
                .filter(item ->
                        item.getEstado()
                                == EstadoItemCatalogo.PENDIENTE
                )
                .sorted(
                        Comparator.comparing(
                                ItemCatalogo::getIdItemCatalogo
                        )
                )
                .map(this::toItemPreviewDTO)
                .toList();
    }

    /**
     * Precio mostrado:
     *
     * Si existe una puja persistida para el lote:
     * muestra la puja de mayor importe.
     *
     * Si no existen pujas:
     * muestra el precio base.
     */
    private Float obtenerPrecioMostrado(
            ItemCatalogo itemCatalogo
    ) {
        if (itemCatalogo == null) {
            return null;
        }

        return pujaRepository
                .findTopByItemCatalogoOrderByMontoDesc(itemCatalogo)
                .map(puja -> puja.getMonto())
                .orElse(itemCatalogo.getPrecioBase());
    }

    private String obtenerImagenPrincipal(
            ItemCatalogo itemCatalogo
    ) {
        if (itemCatalogo == null
                || itemCatalogo.getItem() == null) {

            return null;
        }

        return itemCatalogo
                .getItem()
                .getPrimeraImagen();
    }

    private List<String> obtenerImagenesItem(
            Item item
    ) {
        if (item.getImagenesUrl() != null
                && !item.getImagenesUrl().isEmpty()) {

            return item.getImagenesUrl();
        }

        if (item.getImagenUrl() != null
                && !item.getImagenUrl().isBlank()) {

            return List.of(
                    item.getImagenUrl()
            );
        }

        return List.of();
    }

    private String obtenerTituloCard(
            Subasta subasta
    ) {
        return obtenerTituloSubasta(subasta);
    }

    private String obtenerTituloSubasta(
            Subasta subasta
    ) {
        if (subasta.getCatalogo() != null
                && subasta.getCatalogo().getDescripcion() != null
                && !subasta.getCatalogo()
                .getDescripcion()
                .isBlank()) {

            return subasta
                    .getCatalogo()
                    .getDescripcion();
        }

        return "Subasta #"
                + subasta.getIdSubasta();
    }

    private String obtenerNombreRematador(
            Subasta subasta
    ) {
        if (subasta.getRematador() == null) {
            return null;
        }

        return subasta.getRematador().getNombre()
                + " "
                + subasta.getRematador().getApellido();
    }

    /**
     * Calcula el número de lote según su posición
     * en el catálogo ordenado por ID.
     */
    private Integer obtenerNumeroLote(
            ItemCatalogo itemCatalogo
    ) {
        if (itemCatalogo == null
                || itemCatalogo.getCatalogo() == null
                || itemCatalogo.getCatalogo().getItems() == null) {

            return null;
        }

        List<ItemCatalogo> items =
                new ArrayList<>(
                        itemCatalogo
                                .getCatalogo()
                                .getItems()
                );

        items.sort(
                Comparator.comparing(
                        ItemCatalogo::getIdItemCatalogo
                )
        );

        int indice =
                items.indexOf(itemCatalogo);

        return indice >= 0
                ? indice + 1
                : null;
    }

    private boolean estaGuardada(
            Subasta subasta,
            Usuario usuarioActual
    ) {
        if (usuarioActual == null
                || usuarioActual.getGuardadas() == null) {

            return false;
        }

        return usuarioActual
                .getGuardadas()
                .stream()
                .anyMatch(guardada ->
                        guardada
                                .getIdSubasta()
                                .equals(
                                        subasta.getIdSubasta()
                                )
                );
    }
}