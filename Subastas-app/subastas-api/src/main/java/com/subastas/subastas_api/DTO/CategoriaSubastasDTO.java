package com.subastas.subastas_api.DTO;

import java.util.List;

public class CategoriaSubastasDTO {

    private String nombreCategoria;
    private List<SubastaHomeDTO> tiempoReal;
    private List<SubastaHomeDTO> programadas;

    public CategoriaSubastasDTO(String nombreCategoria,
                                List<SubastaHomeDTO> tiempoReal,
                                List<SubastaHomeDTO> programadas) {
        this.nombreCategoria = nombreCategoria;
        this.tiempoReal = tiempoReal;
        this.programadas = programadas;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public List<SubastaHomeDTO> getTiempoReal() {
        return tiempoReal;
    }

    public List<SubastaHomeDTO> getProgramadas() {
        return programadas;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public void setTiempoReal(List<SubastaHomeDTO> tiempoReal) {
        this.tiempoReal = tiempoReal;
    }

    public void setProgramadas(List<SubastaHomeDTO> programadas) {
        this.programadas = programadas;
    }
}