package com.subastas.subastas_api.model;

public enum EstadoDevolucion {

    /*
     * La devolución fue creada, pero el usuario todavía
     * no eligió dirección y medio de pago.
     */
    PENDIENTE_CONFIGURACION,

    /*
     * El usuario configuró la devolución y debe pagarla.
     */
    PENDIENTE_PAGO,

    /*
     * El pago fue confirmado.
     */
    PAGO_CONFIRMADO,

    /*
     * La empresa despachó el producto.
     */
    ENVIADA,

    /*
     * La empresa confirmó que llegó al usuario.
     */
    ENTREGADA,

    CANCELADA
}