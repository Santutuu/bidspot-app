package com.subastas.subastas_api.model;

public enum EstadoVenta {
    PENDIENTE_PAGO,
    PAGO_CONFIRMADO,
    PREPARANDO_ENVIO,
    ENVIADO,
    EN_CAMINO,
    ENTREGADO,
    MULTADA,
    INCUMPLIDA,
    CANCELADA
}