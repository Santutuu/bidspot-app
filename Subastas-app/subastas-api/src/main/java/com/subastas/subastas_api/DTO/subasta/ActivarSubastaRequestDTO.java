package com.subastas.subastas_api.DTO.subasta;

public class ActivarSubastaRequestDTO {

    /*
     * Es opcional.
     *
     * Si se envía, se abre específicamente ese lote.
     *
     * Si no se envía, el backend abre el primer lote
     * PENDIENTE del catálogo.
     */
    private Long idItemCatalogo;

    public ActivarSubastaRequestDTO() {
    }

    public Long getIdItemCatalogo() {
        return idItemCatalogo;
    }

    public void setIdItemCatalogo(
            Long idItemCatalogo
    ) {
        this.idItemCatalogo =
                idItemCatalogo;
    }
}