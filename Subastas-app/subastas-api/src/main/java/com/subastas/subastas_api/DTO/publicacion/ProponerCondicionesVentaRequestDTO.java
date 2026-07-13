package com.subastas.subastas_api.DTO.publicacion;

public class ProponerCondicionesVentaRequestDTO {

    private Long idSubasta;

    private Float precioBase;

    private Float porcentajeComision;

    public ProponerCondicionesVentaRequestDTO() {
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public Float getPrecioBase() {
        return precioBase;
    }

    public Float getPorcentajeComision() {
        return porcentajeComision;
    }

    public void setIdSubasta(
            Long idSubasta
    ) {
        this.idSubasta = idSubasta;
    }

    public void setPrecioBase(
            Float precioBase
    ) {
        this.precioBase = precioBase;
    }

    public void setPorcentajeComision(
            Float porcentajeComision
    ) {
        this.porcentajeComision =
                porcentajeComision;
    }
}