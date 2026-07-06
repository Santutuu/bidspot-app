package com.subastas.subastas_api.DTO.poliza;

import com.subastas.subastas_api.model.EstadoPoliza;

public class PolizaSolicitudResponseDTO {

    private Long idSolicitud;
    private Long idItem;
    private String tituloItem;
    private Long idPoliza;
    private String nroPoliza;
    private Float montoAsegurado;
    private Float premio;
    private Float precioBase;
    private Float tasaSeguro;
    private String compania;
    private EstadoPoliza estado;

    public PolizaSolicitudResponseDTO(Long idSolicitud,
                                      Long idItem,
                                      String tituloItem,
                                      Long idPoliza,
                                      String nroPoliza,
                                      Float montoAsegurado,
                                      Float premio,
                                      Float precioBase,
                                      Float tasaSeguro,
                                      String compania,
                                      EstadoPoliza estado) {
        this.idSolicitud = idSolicitud;
        this.idItem = idItem;
        this.tituloItem = tituloItem;
        this.idPoliza = idPoliza;
        this.nroPoliza = nroPoliza;
        this.montoAsegurado = montoAsegurado;
        this.premio = premio;
        this.precioBase = precioBase;
        this.tasaSeguro = tasaSeguro;
        this.compania = compania;
        this.estado = estado;
    }

    public Long getIdSolicitud() { return idSolicitud; }
    public Long getIdItem() { return idItem; }
    public String getTituloItem() { return tituloItem; }
    public Long getIdPoliza() { return idPoliza; }
    public String getNroPoliza() { return nroPoliza; }
    public Float getMontoAsegurado() { return montoAsegurado; }
    public Float getPremio() { return premio; }
    public Float getPrecioBase() { return precioBase; }
    public Float getTasaSeguro() { return tasaSeguro; }
    public String getCompania() { return compania; }
    public EstadoPoliza getEstado() { return estado; }
}
