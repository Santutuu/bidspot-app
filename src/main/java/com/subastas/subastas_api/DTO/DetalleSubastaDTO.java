package com.subastas.subastas_api.DTO;

import com.subastas.subastas_api.model.Subasta;
import java.util.List;

public class DetalleSubastaDTO {

    private Long idArticulo;
    private Long idSubasta;

    private String titulo;
    private String descripcion;
    private List<String> imagenesUrl;

    private String moneda;
    private Float precioActual;
    private Float precioInicial;

    private String estadoSubasta;
    private String martillero;

    private String linkStreaming;

    public DetalleSubastaDTO(Long idArticulo,
                             Long idSubasta,
                             String titulo,
                             String descripcion,
                             List<String> imagenesUrl,
                             String moneda,
                             Float precioActual,
                             Float precioInicial,
                             String estadoSubasta,
                             String martillero,
                             String linkStreaming) {

        this.idArticulo = idArticulo;
        this.idSubasta = idSubasta;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagenesUrl = imagenesUrl;
        this.moneda = moneda;
        this.precioActual = precioActual;
        this.precioInicial = precioInicial;
        this.estadoSubasta = estadoSubasta;
        this.martillero = martillero;
        this.linkStreaming = linkStreaming;
    }

    public static DetalleSubastaDTO fromEntity(Subasta subasta) {

        Float precioActual = subasta.getPujaActual() != null
                ? subasta.getPujaActual().getMonto()
                : subasta.getPrecioInicial();

        String martillero = subasta.getRematador().getNombre()
                + " "
                + subasta.getRematador().getApellido();

        return new DetalleSubastaDTO(
                subasta.getItem().getIdItem(),
                subasta.getIdSubasta(),
                subasta.getItem().getTitulo(),
                subasta.getItem().getDescripcion(),
                subasta.getItem().getImagenesUrl(),
                subasta.getMoneda().toString(),
                precioActual,
                subasta.getPrecioInicial(),
                subasta.getEstadoSubasta().toString(),
                martillero,
                "https://streaming.example.com/subasta/" + subasta.getIdSubasta()
        );
    }

    public Long getIdArticulo() {
        return idArticulo;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public List<String> getImagenesUrl() {
        return imagenesUrl;
    }

    public String getMoneda() {
        return moneda;
    }

    public Float getPrecioActual() {
        return precioActual;
    }

    public Float getPrecioInicial() {
        return precioInicial;
    }

    public String getEstadoSubasta() {
        return estadoSubasta;
    }

    public String getMartillero() {
        return martillero;
    }

    public String getLinkStreaming() {
        return linkStreaming;
    }
}