package com.subastas.subastas_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Moneda {
    ARS,
    PESOS,
    DOLARES;

    @JsonCreator
    public static Moneda fromValue(String value) {
        if (value == null) {
            return null;
        }

        return switch (value.trim().toUpperCase()) {
            case "ARS", "PESOS" -> ARS;
            case "USD", "DOLARES" -> DOLARES;
            default -> Moneda.valueOf(value.trim().toUpperCase());
        };
    }

    public Moneda normalizada() {
        if (this == PESOS) {
            return ARS;
        }

        return this;
    }

    public boolean esMismaMoneda(Moneda otra) {
        return otra != null && this.normalizada() == otra.normalizada();
    }
}
