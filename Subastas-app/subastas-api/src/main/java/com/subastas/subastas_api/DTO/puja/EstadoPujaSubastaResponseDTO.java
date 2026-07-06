package com.subastas.subastas_api.DTO.puja;

public class EstadoPujaSubastaResponseDTO {

    private Long idSubasta;
    private Long idItemCatalogo;

    private Float precioBase;
    private Float mejorOferta;
    private Float incrementoMinimo;
    private Float incrementoMaximo;
    private Float ofertaMinimaPermitida;
    private Float ofertaMaximaPermitida;

    private String moneda;

    private Float miMejorOferta;
    private boolean soyMejorPostor;

    public EstadoPujaSubastaResponseDTO(Long idSubasta,
                                        Long idItemCatalogo,
                                        Float precioBase,
                                        Float mejorOferta,
                                        Float incrementoMinimo,
                                        Float incrementoMaximo,
                                        Float ofertaMinimaPermitida,
                                        Float ofertaMaximaPermitida,
                                        String moneda,
                                        Float miMejorOferta,
                                        boolean soyMejorPostor) {
        this.idSubasta = idSubasta;
        this.idItemCatalogo = idItemCatalogo;
        this.precioBase = precioBase;
        this.mejorOferta = mejorOferta;
        this.incrementoMinimo = incrementoMinimo;
        this.incrementoMaximo = incrementoMaximo;
        this.ofertaMinimaPermitida = ofertaMinimaPermitida;
        this.ofertaMaximaPermitida = ofertaMaximaPermitida;
        this.moneda = moneda;
        this.miMejorOferta = miMejorOferta;
        this.soyMejorPostor = soyMejorPostor;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public Long getIdItemCatalogo() {
        return idItemCatalogo;
    }

    public Float getPrecioBase() {
        return precioBase;
    }

    public Float getMejorOferta() {
        return mejorOferta;
    }

    public Float getIncrementoMinimo() {
        return incrementoMinimo;
    }

    public Float getIncrementoMaximo() {
        return incrementoMaximo;
    }

    public Float getOfertaMinimaPermitida() {
        return ofertaMinimaPermitida;
    }

    public Float getOfertaMaximaPermitida() {
        return ofertaMaximaPermitida;
    }

    public String getMoneda() {
        return moneda;
    }

    public Float getMiMejorOferta() {
        return miMejorOferta;
    }

    public boolean isSoyMejorPostor() {
        return soyMejorPostor;
    }
}