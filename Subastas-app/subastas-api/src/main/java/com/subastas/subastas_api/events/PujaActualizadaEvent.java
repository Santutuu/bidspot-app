package com.subastas.subastas_api.events;

import com.subastas.subastas_api.DTO.puja.PujaActualizadaEventDTO;

public class PujaActualizadaEvent {

    private final PujaActualizadaEventDTO payload;

    public PujaActualizadaEvent(PujaActualizadaEventDTO payload) {
        this.payload = payload;
    }

    public PujaActualizadaEventDTO getPayload() {
        return payload;
    }
}