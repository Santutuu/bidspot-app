package com.subastas.subastas_api.model;

public enum EstadoSolicitud {

    /*
     * El usuario creó la solicitud y espera que la empresa
     * determine si está interesada.
     */
    PENDIENTE_REVISION,

    /*
     * La empresa manifestó interés y cargó la dirección
     * donde debe enviarse el producto.
     */
    INTERES_EMPRESA,

    /*
     * El usuario aceptó enviar el producto y aceptó que,
     * si fuera rechazado, la devolución será a su cargo.
     */
    PENDIENTE_ENVIO,

    /*
     * La empresa confirmó la recepción física del producto.
     */
    EN_INSPECCION,

    /*
     * La empresa aprobó el producto y propuso:
     * subasta, precio base y comisión.
     */
    PENDIENTE_CONDICIONES_VENTA,

    /*
     * El usuario aceptó las condiciones.
     * El producto ya puede existir en el modelo legacy y
     * queda pendiente contratar y confirmar la póliza.
     */
    PENDIENTE_POLIZA,

    /*
     * Condiciones y póliza confirmadas.
     * El producto está listo para participar en la subasta.
     */
    LISTA_PARA_SUBASTA,

    /*
     * El producto se encuentra en poder de la empresa
     * y debe ser devuelto al usuario.
     */
    DEVOLUCION_PENDIENTE,

    /*
     * La empresa confirmó que el producto fue entregado
     * nuevamente al usuario.
     */
    DEVUELTA,

    /*
     * La empresa rechazó la solicitud antes de recibir
     * físicamente el producto.
     */
    RECHAZADA,

    /*
     * Cancelación solicitada por el usuario antes de
     * generar obligaciones físicas o económicas.
     */
    CANCELADA
}