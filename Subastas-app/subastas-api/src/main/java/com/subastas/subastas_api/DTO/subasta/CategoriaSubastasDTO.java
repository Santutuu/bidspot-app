package com.subastas.subastas_api.DTO.subasta;

import java.util.List;

public class CategoriaSubastasDTO {

    private List<SubastaHomeDTO> activas;
    private List<SubastaHomeDTO> programadas;

    public CategoriaSubastasDTO(List<SubastaHomeDTO> activas,
                                List<SubastaHomeDTO> programadas) {
        this.activas = activas;
        this.programadas = programadas;
    }

    public List<SubastaHomeDTO> getActivas() {
        return activas;
    }

    public List<SubastaHomeDTO> getProgramadas() {
        return programadas;
    }
}