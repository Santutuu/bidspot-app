package com.subastas.subastas_api.DTO.subasta;

import com.subastas.subastas_api.DTO.subasta.*;


import java.util.List;

public class DetalleSubastaDTO {

    private SubastaInfoDTO subasta;
    private ItemActualDTO itemActual;
    private List<ItemCatalogoPreviewDTO> catalogo;
    private List<ItemCatalogoPreviewDTO> proximosLotes;

    public DetalleSubastaDTO(SubastaInfoDTO subasta,
                             ItemActualDTO itemActual,
                             List<ItemCatalogoPreviewDTO> catalogo,
                             List<ItemCatalogoPreviewDTO> proximosLotes) {
        this.subasta = subasta;
        this.itemActual = itemActual;
        this.catalogo = catalogo;
        this.proximosLotes = proximosLotes;
    }

    public SubastaInfoDTO getSubasta() {
        return subasta;
    }

    public ItemActualDTO getItemActual() {
        return itemActual;
    }

    public List<ItemCatalogoPreviewDTO> getCatalogo() {
        return catalogo;
    }

    public List<ItemCatalogoPreviewDTO> getProximosLotes() {
        return proximosLotes;
    }
}