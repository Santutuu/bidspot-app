package com.subastas.subastas_api.DTO;

import com.subastas.subastas_api.model.EstadoSubasta;
import com.subastas.subastas_api.model.Subasta;

import java.text.SimpleDateFormat;
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
    private Float precioMostrado;
    private String tipoPrecio;

    private String estadoSubasta;
    private boolean puedeOfertar;

    private String fechaInicio;
    private String horaInicio;

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
                             Float precioMostrado,
                             String tipoPrecio,
                             String estadoSubasta,
                             boolean puedeOfertar,
                             String fechaInicio,
                             String horaInicio,
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
        this.precioMostrado = precioMostrado;
        this.tipoPrecio = tipoPrecio;
        this.estadoSubasta = estadoSubasta;
        this.puedeOfertar = puedeOfertar;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.martillero = martillero;
        this.linkStreaming = linkStreaming;
    }

    public static DetalleSubastaDTO fromEntity(Subasta subasta) {
        Float precioInicial = subasta.getPrecioInicial();

        Float precioActual = subasta.getPujaActual() != null
                ? subasta.getPujaActual().getMonto()
                : null;

        Float precioMostrado;
        String tipoPrecio;
        boolean puedeOfertar;

        if (subasta.getEstadoSubasta() == EstadoSubasta.ACTIVA) {
            precioMostrado = precioActual != null ? precioActual : precioInicial;
            tipoPrecio = "PRECIO_ACTUAL";
            puedeOfertar = true;
        } else if (subasta.getEstadoSubasta() == EstadoSubasta.CREADA) {
            precioMostrado = precioInicial;
            tipoPrecio = "PRECIO_INICIAL";
            puedeOfertar = false;
        } else {
            precioMostrado = precioActual != null ? precioActual : precioInicial;
            tipoPrecio = "PRECIO_FINAL";
            puedeOfertar = false;
        }

        String martillero = subasta.getRematador().getNombre()
                + " "
                + subasta.getRematador().getApellido();

        SimpleDateFormat fechaFormatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat horaFormatter = new SimpleDateFormat("HH:mm");

        String fechaInicio = subasta.getFechaInicio() != null
                ? fechaFormatter.format(subasta.getFechaInicio())
                : null;

        String horaInicio = subasta.getHoraInicio() != null
                ? horaFormatter.format(subasta.getHoraInicio())
                : null;

        return new DetalleSubastaDTO(
                subasta.getItem().getIdItem(),
                subasta.getIdSubasta(),
                subasta.getItem().getTitulo(),
                subasta.getItem().getDescripcion(),
                subasta.getItem().getImagenesUrl(),
                subasta.getMoneda().toString(),
                precioActual,
                precioInicial,
                precioMostrado,
                tipoPrecio,
                subasta.getEstadoSubasta().toString(),
                puedeOfertar,
                fechaInicio,
                horaInicio,
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

    public Float getPrecioMostrado() {
        return precioMostrado;
    }

    public String getTipoPrecio() {
        return tipoPrecio;
    }

    public String getEstadoSubasta() {
        return estadoSubasta;
    }

    public boolean isPuedeOfertar() {
        return puedeOfertar;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getMartillero() {
        return martillero;
    }

    public String getLinkStreaming() {
        return linkStreaming;
    }
}