package com.subastas.subastas_api.mapper;

import com.subastas.subastas_api.DTO.subasta.*;
import com.subastas.subastas_api.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubastaMapper {

    public SubastaHomeDTO toHomeDTO(Subasta subasta) {
        ItemCatalogo itemVisible = obtenerItemVisibleParaCard(subasta);

        return new SubastaHomeDTO(
                subasta.getIdSubasta(),
                obtenerTituloCard(subasta, itemVisible),
                obtenerImagenPrincipal(itemVisible),
                obtenerPrecioMostrado(itemVisible),
                subasta.getMoneda() != null ? subasta.getMoneda().toString() : null,
                subasta.getEstadoSubasta(),
                subasta.getCategoriaMin(),
                subasta.getFechaInicio()
        );
    }

    public DetalleSubastaDTO toDetalleDTO(Subasta subasta) {
        SubastaInfoDTO subastaInfo = toSubastaInfoDTO(subasta);

        if (subasta.getEstadoSubasta() == EstadoSubasta.ACTIVA) {
            ItemCatalogo itemActual = obtenerItemActual(subasta);

            return new DetalleSubastaDTO(
                    subastaInfo,
                    itemActual != null ? toItemActualDTO(itemActual) : null,
                    List.of(),
                    obtenerProximosLotes(subasta)
            );
        }

        if (subasta.getEstadoSubasta() == EstadoSubasta.PROGRAMADA) {
            return new DetalleSubastaDTO(
                    subastaInfo,
                    null,
                    obtenerCatalogoCompleto(subasta),
                    List.of()
            );
        }

        return new DetalleSubastaDTO(
                subastaInfo,
                null,
                List.of(),
                List.of()
        );
    }

    public ItemCatalogoPreviewDTO toItemPreviewDTO(ItemCatalogo itemCatalogo) {
        Item item = itemCatalogo.getItem();

        return new ItemCatalogoPreviewDTO(
                itemCatalogo.getIdItemCatalogo(),
                obtenerNumeroLote(itemCatalogo),
                item != null ? item.getTitulo() : null,
                item != null ? item.getPrimeraImagen() : null,
                itemCatalogo.getPrecioBase()
        );
    }

    public ItemActualDTO toItemActualDTO(ItemCatalogo itemCatalogo) {
        Item item = itemCatalogo.getItem();

        return new ItemActualDTO(
                itemCatalogo.getIdItemCatalogo(),
                obtenerNumeroLote(itemCatalogo),
                item != null ? item.getTitulo() : null,
                item != null ? item.getDescripcion() : null,
                item != null ? item.getImagenesUrl() : List.of(),
                itemCatalogo.getPrecioBase(),
                obtenerPrecioMostrado(itemCatalogo)
        );
    }

    private SubastaInfoDTO toSubastaInfoDTO(Subasta subasta) {
        return new SubastaInfoDTO(
                subasta.getIdSubasta(),
                obtenerTituloSubasta(subasta),
                subasta.getEstadoSubasta(),
                subasta.getCategoriaMin(),
                subasta.getMoneda() != null ? subasta.getMoneda().toString() : null,
                subasta.getFechaInicio(),
                subasta.getUbicacion(),
                obtenerNombreRematador(subasta),
                subasta.getEstadoSubasta() == EstadoSubasta.ACTIVA ? subasta.getLinkVivo() : null
        );
    }

    private ItemCatalogo obtenerItemVisibleParaCard(Subasta subasta) {
        if (subasta.getEstadoSubasta() == EstadoSubasta.ACTIVA) {
            ItemCatalogo itemActual = obtenerItemActual(subasta);
            if (itemActual != null) {
                return itemActual;
            }
        }

        return obtenerPrimerItem(subasta);
    }

    private ItemCatalogo obtenerItemActual(Subasta subasta) {
        if (subasta.getCatalogo() == null || subasta.getCatalogo().getItems() == null) {
            return null;
        }

        return subasta.getCatalogo()
                .getItems()
                .stream()
                .filter(item -> item.getEstado() == EstadoItemCatalogo.EN_REMATE)
                .findFirst()
                .orElse(null);
    }

    private ItemCatalogo obtenerPrimerItem(Subasta subasta) {
        if (subasta.getCatalogo() == null || subasta.getCatalogo().getItems() == null || subasta.getCatalogo().getItems().isEmpty()) {
            return null;
        }

        return subasta.getCatalogo().getItems().get(0);
    }

    private List<ItemCatalogoPreviewDTO> obtenerCatalogoCompleto(Subasta subasta) {
        if (subasta.getCatalogo() == null || subasta.getCatalogo().getItems() == null) {
            return List.of();
        }

        return subasta.getCatalogo()
                .getItems()
                .stream()
                .map(this::toItemPreviewDTO)
                .toList();
    }

    private List<ItemCatalogoPreviewDTO> obtenerProximosLotes(Subasta subasta) {
        if (subasta.getCatalogo() == null || subasta.getCatalogo().getItems() == null) {
            return List.of();
        }

        return subasta.getCatalogo()
                .getItems()
                .stream()
                .filter(item -> item.getEstado() == EstadoItemCatalogo.PENDIENTE)
                .map(this::toItemPreviewDTO)
                .toList();
    }

    private Float obtenerPrecioMostrado(ItemCatalogo itemCatalogo) {
        if (itemCatalogo == null) {
            return null;
        }

        if (itemCatalogo.getPujaActual() != null) {
            return itemCatalogo.getPujaActual().getMonto();
        }

        return itemCatalogo.getPrecioBase();
    }

    private String obtenerImagenPrincipal(ItemCatalogo itemCatalogo) {
        if (itemCatalogo == null || itemCatalogo.getItem() == null) {
            return null;
        }

        return itemCatalogo.getItem().getPrimeraImagen();
    }

    private String obtenerTituloCard(Subasta subasta, ItemCatalogo itemVisible) {
        if (subasta.getCatalogo() != null &&
                subasta.getCatalogo().getDescripcion() != null &&
                !subasta.getCatalogo().getDescripcion().isBlank()) {
            return subasta.getCatalogo().getDescripcion();
        }

        return obtenerTituloSubasta(subasta);
    }

    private String obtenerTituloSubasta(Subasta subasta) {
        return "Subasta #" + subasta.getIdSubasta();
    }

    private String obtenerNombreRematador(Subasta subasta) {
        if (subasta.getRematador() == null) {
            return null;
        }

        return subasta.getRematador().getNombre() + " " + subasta.getRematador().getApellido();
    }

    private Integer obtenerNumeroLote(ItemCatalogo itemCatalogo) {
        if (itemCatalogo == null || itemCatalogo.getCatalogo() == null || itemCatalogo.getCatalogo().getItems() == null) {
            return null;
        }

        List<ItemCatalogo> items = new ArrayList<>(itemCatalogo.getCatalogo().getItems());

        return items.indexOf(itemCatalogo) + 1;
    }
}